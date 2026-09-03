package com.unicine.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.user.ClienteServicio;
import com.unicine.transfer.dto.request.ClienteRequest;
import com.unicine.transfer.dto.response.ClienteResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller para perfil propio y administracion de clientes.
 * Ownership: /me usa el principal; /{cedula} solo si cedula == principal o rol ADMIN.
 */
@RestController
@RequestMapping("/api/clientes")
@Validated
@Tag(name = "Clientes", description = "Perfil y administración de clientes")
public class ClienteController {

    private final ClienteServicio clienteServicio;

    public ClienteController(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }

    // SECTION: Perfil propio

    @GetMapping("/me")
    @Operation(summary = "Obtener mi perfil", description = "Requiere autenticación. Devuelve el cliente del principal sin password.")
    public ResponseEntity<ClienteResponse> obtenerMiPerfil(@AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return clienteServicio.obtener(principal.getCedula())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar mi perfil", description = "Ignora cedula del body, usa la del principal. Valida duplicados y edad.")
    public ResponseEntity<ClienteResponse> actualizarMiPerfil(
            @AuthenticationPrincipal UsuarioPrincipal principal,
            @Valid @RequestBody ClienteRequest request) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        request.setCedula(principal.getCedula());
        request.setEstado(true);
        ClienteResponse response = clienteServicio.actualizar(request);
        return ResponseEntity.ok(response);
    }

    // !SECTION
    // SECTION: Administración protegida

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Requiere rol ADMIN. Por ahora authenticated().")
    public ResponseEntity<List<ClienteResponse>> listar(Authentication auth) {
        // TODO 5.2: @PreAuthorize("hasRole('ADMINISTRADOR')")
        return ResponseEntity.ok(clienteServicio.listar());
    }

    @GetMapping("/{cedula}")
    @Operation(summary = "Obtener cliente por cédula", description = "Solo propio o ADMIN. Por ahora valida ownership manual.")
    public ResponseEntity<ClienteResponse> obtenerPorCedula(
            @PathVariable @Positive Integer cedula,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        // Si no es ADMIN y pide otro cedula -> 403
        boolean esAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        if (!esAdmin && !principal.getCedula().equals(cedula)) {
            return ResponseEntity.status(403).build();
        }
        return clienteServicio.obtener(cedula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{cedula}")
    @Operation(summary = "Eliminar cliente — requiere ?confirmacion=true", description = "Solo propio o ADMIN.")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Integer cedula,
            @RequestParam boolean confirmacion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        boolean esAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        if (!esAdmin && !principal.getCedula().equals(cedula)) {
            return ResponseEntity.status(403).build();
        }
        clienteServicio.eliminar(cedula, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
}
