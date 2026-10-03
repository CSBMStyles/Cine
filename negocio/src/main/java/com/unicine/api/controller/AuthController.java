package com.unicine.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unicine.entity.user.Persona;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.user.AdministradorServicio;
import com.unicine.service.user.ClienteServicio;
import com.unicine.security.JwtServicio;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.auth.RefrescoServicio;
import com.unicine.service.user.AuthenticationService;
import com.unicine.transfer.dto.auth.ParTokensResponse;
import com.unicine.transfer.dto.auth.RefreshRequest;
import com.unicine.transfer.dto.auth.LoginRequest;
import com.unicine.transfer.dto.auth.LoginResponse;
import com.unicine.transfer.dto.request.ClienteRequest;
import com.unicine.transfer.dto.response.ClienteResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller de autenticacion — unico punto para registro y login.
 * Rutas permitAll en SecurityConfig para POST /api/auth/**
 */
@RestController
@RequestMapping("/api/auth")
@Validated
@Tag(name = "Autenticación", description = "Registro y login con access token JWT (5.1)")
public class AuthController {

    private final ClienteServicio clienteServicio;
    private final AdministradorServicio administradorServicio;
    private final AuthenticationService authenticationService;
    private final JwtServicio jwtServicio;
    private final RefrescoServicio refrescoServicio;

    public AuthController(ClienteServicio clienteServicio,
                          AdministradorServicio administradorServicio,
                          AuthenticationService authenticationService,
                          JwtServicio jwtServicio,
                          RefrescoServicio refrescoServicio) {
        this.clienteServicio = clienteServicio;
        this.administradorServicio = administradorServicio;
        this.authenticationService = authenticationService;
        this.jwtServicio = jwtServicio;
        this.refrescoServicio = refrescoServicio;
    }

    // SECTION: Registro

    /**
     * Registro unico para cliente.
     * Admin se crea via data.sql o endpoint protegido futuro 4.3.3.
     */
    @PostMapping("/registro")
    @Operation(summary = "Registrar cliente", description = "Crea un cliente. Valida password fuerte, edad >18, duplicados. No expone password.")
    public ResponseEntity<ClienteResponse> registro(@Valid @RequestBody ClienteRequest request) throws Exception {
        // Forzar estado activo — el cliente no elige su estado
        request.setEstado(true);
        ClienteResponse response = clienteServicio.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // !SECTION
    // SECTION: Login

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Un solo formulario correo+password. Resuelve tipo CLIENTE/ADMIN/ADMIN_TEATRO y crea sesion con par access+refresh.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        Persona persona = authenticationService.login(request.getCorreo(), request.getPassword());

        TipoUsuario tipo;
        String simpleName = persona.getClass().getSimpleName();
        switch (simpleName) {
            case "Cliente":
                tipo = TipoUsuario.CLIENTE;
                break;
            case "Administrador":
                tipo = TipoUsuario.ADMINISTRADOR;
                break;
            case "AdministradorTeatro":
                tipo = TipoUsuario.ADMINISTRADOR_TEATRO;
                break;
            default:
                tipo = TipoUsuario.CLIENTE;
                break;
        }

        java.util.List<Integer> teatroIds = extraerTeatroIds(persona);
        UsuarioPrincipal principal = new UsuarioPrincipal(
                persona.getCedula(), persona.getCorreo(), null, tipo, teatroIds);

        ParTokensResponse par = refrescoServicio.crearSesion(principal);

        LoginResponse response = LoginResponse.builder()
                .cedula(persona.getCedula())
                .nombre(persona.getNombre())
                .correo(persona.getCorreo())
                .tipo(tipo)
                .teatroIds(teatroIds)
                .accessToken(par.getAccessToken())
                .refreshToken(par.getRefreshToken())
                .mensaje("Autenticado correctamente.")
                .build();

        return ResponseEntity.ok(response);
    }

    // !SECTION
    // SECTION: Refresh y logout

    @PostMapping("/refresh")
    @Operation(summary = "Rotar sesion", description = "Revoca el refresh usado y emite un par nuevo. Reusar un refresh revocado revoca todas las sesiones.")
    public ResponseEntity<ParTokensResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(refrescoServicio.rotar(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesion", description = "Revoca el refresh en servidor. Idempotente: siempre 200.")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        refrescoServicio.cerrarSesion(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    // !SECTION

    private java.util.List<Integer> extraerTeatroIds(Persona persona) {
        if (persona instanceof com.unicine.entity.user.AdministradorTeatro adminTeatro
                && adminTeatro.getTeatros() != null) {
            return adminTeatro.getTeatros().stream()
                    .filter(t -> t != null && t.getCodigo() != null)
                    .map(t -> t.getCodigo())
                    .toList();
        }
        return java.util.List.of();
    }

    // !SECTION
}
