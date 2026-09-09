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
import com.unicine.service.theater.DistribucionSillaServicio;
import com.unicine.transfer.dto.request.DistribucionSillaRequest;
import com.unicine.transfer.dto.response.DistribucionSillaResponse;
import com.unicine.util.pagination.PaginadoManual;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller de distribuciones de silla — layouts de sala.
 * Todo requiere autenticacion; escritura solo ADMIN o ADMINISTRADOR_TEATRO.
 */
@RestController
@RequestMapping("/api/distribuciones-silla")
@Validated
@Tag(name = "Distribuciones de silla", description = "Layouts de sillas por sala")
public class DistribucionSillaController {

    private final DistribucionSillaServicio distribucionServicio;

    public DistribucionSillaController(DistribucionSillaServicio distribucionServicio) {
        this.distribucionServicio = distribucionServicio;
    }

    // SECTION: Escritura protegida

    @PostMapping
    @Operation(summary = "Registrar distribución", description = "Solo ADMIN o ADMINISTRADOR_TEATRO.")
    public ResponseEntity<DistribucionSillaResponse> registrar(
            @Valid @RequestBody DistribucionSillaRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(distribucionServicio.registrar(request));
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar distribución", description = "Solo ADMIN o ADMINISTRADOR_TEATRO.")
    public ResponseEntity<DistribucionSillaResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody DistribucionSillaRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        request.setCodigo(codigo);
        return ResponseEntity.ok(distribucionServicio.actualizar(request));
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar distribución — requiere ?confirmacion=true", description = "Solo ADMIN.")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Integer codigo,
            @RequestParam boolean confirmacion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        distribucionServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura autenticada

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener distribución por código")
    public ResponseEntity<DistribucionSillaResponse> obtener(
            @PathVariable @Positive Integer codigo,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return distribucionServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar distribuciones", description = "Requiere autenticación. ?page=&size=")
    public ResponseEntity<List<DistribucionSillaResponse>> listar(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal UsuarioPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(PaginadoManual.paginar(distribucionServicio.listar(), page, size));
    }

    // !SECTION
}
