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
import com.unicine.service.user.AuthenticationService;
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
@Tag(name = "Autenticación", description = "Registro y login — sin JWT aún (Fase 5)")
public class AuthController {

    private final ClienteServicio clienteServicio;
    private final AdministradorServicio administradorServicio;
    private final AuthenticationService authenticationService;

    public AuthController(ClienteServicio clienteServicio,
                          AdministradorServicio administradorServicio,
                          AuthenticationService authenticationService) {
        this.clienteServicio = clienteServicio;
        this.administradorServicio = administradorServicio;
        this.authenticationService = authenticationService;
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
    @Operation(summary = "Login", description = "Un solo formulario correo+password. Resuelve tipo CLIENTE/ADMIN/ADMIN_TEATRO. Sin token hasta Fase 5.")
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

        LoginResponse response = LoginResponse.builder()
                .cedula(persona.getCedula())
                .nombre(persona.getNombre())
                .correo(persona.getCorreo())
                .tipo(tipo)
                .mensaje("Autenticado correctamente. JWT pendiente Fase 5.")
                .build();

        return ResponseEntity.ok(response);
    }

    // !SECTION
}
