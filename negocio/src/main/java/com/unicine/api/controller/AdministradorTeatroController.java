package com.unicine.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.user.AdministradorTeatroServicio;
import com.unicine.transfer.dto.request.AdministradorTeatroRequest;
import com.unicine.transfer.dto.response.AdministradorTeatroResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller para administradores de teatro.
 * Alta por API solo ADMIN (el rol opera teatros); perfil propio y consulta
 * propia o ADMIN.
 */
@RestController
@RequestMapping("/api/administradores-teatro")
@Validated
@Tag(name = "Administradores de teatro", description = "Gestión de administradores de teatro")
public class AdministradorTeatroController {

    private final AdministradorTeatroServicio administradorTeatroServicio;

    public AdministradorTeatroController(AdministradorTeatroServicio administradorTeatroServicio) {
        this.administradorTeatroServicio = administradorTeatroServicio;
    }

    // SECTION: Alta ADMIN

    @PostMapping
    @Operation(summary = "Registrar administrador de teatro", description = "Solo ADMIN.")
    public ResponseEntity<AdministradorTeatroResponse> registrar(
            @Valid @RequestBody AdministradorTeatroRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(administradorTeatroServicio.registrar(request));
    }

    // !SECTION
    // SECTION: Perfil propio

    @GetMapping("/me")
    @Operation(summary = "Obtener mi perfil administrador de teatro")
    public ResponseEntity<AdministradorTeatroResponse> obtenerMiPerfil(
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return administradorTeatroServicio.obtener(principal.getCedula())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar mi perfil administrador de teatro")
    public ResponseEntity<AdministradorTeatroResponse> actualizarMiPerfil(
            @AuthenticationPrincipal UsuarioPrincipal principal,
            @Valid @RequestBody AdministradorTeatroRequest request) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        request.setCedula(principal.getCedula());
        return ResponseEntity.ok(administradorTeatroServicio.actualizar(request));
    }

    // !SECTION
    // SECTION: Administración

    @GetMapping
    @Operation(summary = "Listar administradores de teatro", description = "Solo ADMIN.")
    public ResponseEntity<List<AdministradorTeatroResponse>> listar(
            @AuthenticationPrincipal UsuarioPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(administradorTeatroServicio.listar());
    }

    @GetMapping("/{cedula}")
    @Operation(summary = "Obtener administrador de teatro por cédula", description = "Solo propio o ADMIN.")
    public ResponseEntity<AdministradorTeatroResponse> obtenerPorCedula(
            @PathVariable @Positive Integer cedula,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador() && !principal.getCedula().equals(cedula)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return administradorTeatroServicio.obtener(cedula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{cedula}")
    @Operation(summary = "Eliminar administrador de teatro — requiere ?confirmacion=true",
            description = "Solo ADMIN.")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Integer cedula,
            @RequestParam boolean confirmacion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        administradorTeatroServicio.eliminar(cedula, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
}
