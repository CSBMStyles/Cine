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
import com.unicine.service.purchase.CuponServicio;
import com.unicine.transfer.dto.request.CuponRequest;
import com.unicine.transfer.dto.response.CuponResponse;
import com.unicine.util.pagination.PaginadoManual;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Controller de cupones — lectura autenticada, escritura solo ADMIN.
 * Reglas de cupon viven en el servicio, no aqui.
 */
@RestController
@RequestMapping("/api/cupones")
@Validated
@Tag(name = "Cupones", description = "Cupones globales de descuento")
public class CuponController {

    private final CuponServicio cuponServicio;

    public CuponController(CuponServicio cuponServicio) {
        this.cuponServicio = cuponServicio;
    }

    // SECTION: Escritura ADMIN

    @PostMapping
    @Operation(summary = "Registrar cupón", description = "Solo ADMIN.")
    public ResponseEntity<CuponResponse> registrar(
            @Valid @RequestBody CuponRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(cuponServicio.registrar(request));
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar cupón", description = "Solo ADMIN.")
    public ResponseEntity<CuponResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody CuponRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        request.setCodigo(codigo);
        return ResponseEntity.ok(cuponServicio.actualizar(request));
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar cupón — requiere ?confirmacion=true", description = "Solo ADMIN.")
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
        cuponServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura autenticada

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener cupón por código")
    public ResponseEntity<CuponResponse> obtener(
            @PathVariable @Positive Integer codigo,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return cuponServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/activos")
    @Operation(summary = "Cupones activos")
    public ResponseEntity<List<CuponResponse>> activos(
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(cuponServicio.listarActivos());
    }

    @GetMapping("/vencidos")
    @Operation(summary = "Cupones vencidos")
    public ResponseEntity<List<CuponResponse>> vencidos(
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(cuponServicio.listarVencidos());
    }

    @GetMapping("/con-asignaciones")
    @Operation(summary = "Cupones con al menos una asignación")
    public ResponseEntity<List<CuponResponse>> conAsignaciones(
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(cuponServicio.listarConAsignaciones());
    }

    @GetMapping
    @Operation(summary = "Listar cupones", description = "Filtros: ?criterio=&min=&max= | ?page=&size=")
    public ResponseEntity<List<CuponResponse>> listar(
            @RequestParam(required = false) String criterio,
            @RequestParam(required = false) @PositiveOrZero Double min,
            @RequestParam(required = false) @PositiveOrZero Double max,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (criterio != null && !criterio.isBlank()) {
            return ResponseEntity.ok(cuponServicio.buscarPorCriterio(criterio));
        }
        if (min != null && max != null) {
            return ResponseEntity.ok(cuponServicio.listarPorRangoDescuento(min, max));
        }
        if (page != null || size != null) {
            return ResponseEntity.ok(PaginadoManual.paginar(cuponServicio.listar(), page, size));
        }
        return ResponseEntity.ok(cuponServicio.listar());
    }

    // !SECTION
}
