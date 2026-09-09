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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.confiteria.HistorialPrecioPresentacionServicio;
import com.unicine.transfer.dto.request.HistorialPrecioPresentacionRequest;
import com.unicine.transfer.dto.response.HistorialPrecioPresentacionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller de historial de precios — permisos por rol segun contrato del servicio:
 * cliente y administrador de teatro solo ultimo registro; administrador todo + borrado.
 */
@RestController
@RequestMapping("/api/historial-precios")
@Validated
@Tag(name = "Historial de precios", description = "Historial de precios de presentaciones")
public class HistorialPrecioController {

    private final HistorialPrecioPresentacionServicio historialServicio;

    public HistorialPrecioController(HistorialPrecioPresentacionServicio historialServicio) {
        this.historialServicio = historialServicio;
    }

    // SECTION: Escritura y borrado ADMIN

    @PostMapping
    @Operation(summary = "Registrar entrada de historial", description = "Solo ADMIN.")
    public ResponseEntity<HistorialPrecioPresentacionResponse> registrar(
            @Valid @RequestBody HistorialPrecioPresentacionRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(historialServicio.registrar(request));
    }

    @DeleteMapping
    @Operation(summary = "Eliminar historial por presentación — requiere ?presentacion=&confirmacion=true",
            description = "Solo ADMIN.")
    public ResponseEntity<Void> eliminarPorPresentacion(
            @RequestParam @Positive Integer presentacion,
            @RequestParam boolean confirmacion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!confirmacion) {
            return ResponseEntity.badRequest().build();
        }
        historialServicio.eliminarPorPresentacion(presentacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura por rol

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener entrada por código", description = "Requiere autenticación.")
    public ResponseEntity<HistorialPrecioPresentacionResponse> obtener(
            @PathVariable @Positive Integer codigo,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return historialServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ultimo")
    @Operation(summary = "Último registro por presentación",
            description = "Cliente y administrador de teatro ven % descuento. Requiere autenticación.")
    public ResponseEntity<HistorialPrecioPresentacionResponse> ultimoPorPresentacion(
            @RequestParam @Positive Integer presentacion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return historialServicio.obtenerUltimoPorPresentacion(presentacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Historial completo por presentación", description = "Solo ADMIN.")
    public ResponseEntity<List<HistorialPrecioPresentacionResponse>> listarPorPresentacion(
            @RequestParam @Positive Integer presentacion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(historialServicio.listarPorPresentacion(presentacion));
    }

    // !SECTION
}
