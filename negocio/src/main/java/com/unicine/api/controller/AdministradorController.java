package com.unicine.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
import com.unicine.service.user.AdministradorServicio;
import com.unicine.transfer.dto.request.AdministradorRequest;
import com.unicine.transfer.dto.response.AdministradorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller para administradores.
 * Solo perfil propio + listado/consulta protegidos.
 */
@RestController
@RequestMapping("/api/administradores")
@Validated
@Tag(name = "Administradores", description = "Gestión de administradores")
public class AdministradorController {

    private final AdministradorServicio administradorServicio;

    public AdministradorController(AdministradorServicio administradorServicio) {
        this.administradorServicio = administradorServicio;
    }

    // SECTION: Perfil propio

    @GetMapping("/me")
    @Operation(summary = "Obtener mi perfil administrador")
    public ResponseEntity<AdministradorResponse> obtenerMiPerfil(@AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return administradorServicio.obtener(principal.getCedula())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar mi perfil administrador")
    public ResponseEntity<AdministradorResponse> actualizarMiPerfil(
            @AuthenticationPrincipal UsuarioPrincipal principal,
            @Valid @RequestBody AdministradorRequest request) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        request.setCedula(principal.getCedula());
        AdministradorResponse response = administradorServicio.actualizar(request);
        return ResponseEntity.ok(response);
    }

    // !SECTION
    // SECTION: Administración

    @GetMapping
    @Operation(summary = "Listar administradores")
    public ResponseEntity<List<AdministradorResponse>> listar() {
        return ResponseEntity.ok(administradorServicio.listar());
    }

    @GetMapping("/{cedula}")
    @Operation(summary = "Obtener administrador por cédula")
    public ResponseEntity<AdministradorResponse> obtenerPorCedula(
            @PathVariable @Positive Integer cedula,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return administradorServicio.obtener(cedula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{cedula}")
    @Operation(summary = "Eliminar administrador — requiere ?confirmacion=true")
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
        administradorServicio.eliminar(cedula, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
}
