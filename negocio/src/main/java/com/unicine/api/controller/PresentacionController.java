package com.unicine.api.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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
import com.unicine.service.confiteria.ConfiteriaPresentacionServicio;
import com.unicine.transfer.dto.request.ConfiteriaPresentacionRequest;
import com.unicine.transfer.dto.response.ConfiteriaPresentacionResponse;
import com.unicine.util.pagination.PaginadoManual;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller de presentaciones — catalogo publico + administracion de precios.
 * Cambios de precio solo ADMIN o ADMINISTRADOR_TEATRO (regla del servicio).
 */
@RestController
@RequestMapping("/api/presentaciones")
@Validated
@Tag(name = "Presentaciones", description = "Presentaciones y precios de confitería")
public class PresentacionController {

    private final ConfiteriaPresentacionServicio presentacionServicio;

    public PresentacionController(ConfiteriaPresentacionServicio presentacionServicio) {
        this.presentacionServicio = presentacionServicio;
    }

    // SECTION: Escritura protegida

    @PostMapping
    @Operation(summary = "Registrar presentación", description = "Solo ADMIN o ADMINISTRADOR_TEATRO.")
    public ResponseEntity<ConfiteriaPresentacionResponse> registrar(
            @Valid @RequestBody ConfiteriaPresentacionRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(presentacionServicio.registrar(request));
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar presentación",
            description = "Solo ADMIN o ADMINISTRADOR_TEATRO. ?fechaExpiracion= opcional ISO; si se omite usa la del body.")
    public ResponseEntity<ConfiteriaPresentacionResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody ConfiteriaPresentacionRequest request,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaExpiracion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        request.setCodigo(codigo);
        LocalDateTime expiracion = fechaExpiracion != null ? fechaExpiracion : request.getFechaExpiracionTemporal();
        return ResponseEntity.ok(presentacionServicio.actualizar(request, expiracion));
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar presentación — requiere ?confirmacion=true", description = "Solo ADMIN.")
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
        presentacionServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura publica

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener presentación por código")
    public ResponseEntity<ConfiteriaPresentacionResponse> obtener(
            @PathVariable @Positive Integer codigo) throws Exception {
        return presentacionServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/con-descuento")
    @Operation(summary = "Presentaciones con descuento temporal activo", description = "Catálogo público.")
    public ResponseEntity<List<ConfiteriaPresentacionResponse>> conDescuento() {
        return ResponseEntity.ok(presentacionServicio.listarConDescuentoTemporal());
    }

    @GetMapping
    @Operation(summary = "Listar presentaciones",
            description = "Catálogo público. Filtros: ?confiteria= | ?page=&size=")
    public ResponseEntity<List<ConfiteriaPresentacionResponse>> listar(
            @RequestParam(required = false) Integer confiteria,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) throws Exception {
        if (confiteria != null) {
            return ResponseEntity.ok(presentacionServicio.listarPorConfiteria(confiteria));
        }
        if (page != null || size != null) {
            return ResponseEntity.ok(PaginadoManual.paginar(presentacionServicio.listar(), page, size));
        }
        return ResponseEntity.ok(presentacionServicio.listar());
    }

    // !SECTION
}
