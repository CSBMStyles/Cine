package com.unicine.api.controller;

import java.util.List;
import java.util.Map;

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
import com.unicine.service.purchase.CuponClienteServicio;
import com.unicine.transfer.dto.request.CuponClienteRequest;
import com.unicine.transfer.dto.response.CuponClienteResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller de asignaciones cupon-cliente.
 * Lectura: propio o ADMIN. Escritura y asignacion: solo ADMIN.
 */
@RestController
@RequestMapping("/api/cupones-clientes")
@Validated
@Tag(name = "Cupones de clientes", description = "Asignaciones de cupones a clientes")
public class CuponClienteController {

    private final CuponClienteServicio cuponClienteServicio;

    public CuponClienteController(CuponClienteServicio cuponClienteServicio) {
        this.cuponClienteServicio = cuponClienteServicio;
    }

    // SECTION: Escritura ADMIN

    @PostMapping
    @Operation(summary = "Asignar cupón a cliente", description = "Solo ADMIN.")
    public ResponseEntity<CuponClienteResponse> registrar(
            @Valid @RequestBody CuponClienteRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(cuponClienteServicio.registrar(request));
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar asignación", description = "Solo ADMIN.")
    public ResponseEntity<CuponClienteResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody CuponClienteRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        request.setCodigo(codigo);
        return ResponseEntity.ok(cuponClienteServicio.actualizar(request));
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar asignación — requiere ?confirmacion=true", description = "Solo ADMIN.")
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
        cuponClienteServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura propio o ADMIN

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener asignación por código", description = "Propio o ADMIN.")
    public ResponseEntity<CuponClienteResponse> obtener(
            @PathVariable @Positive Integer codigo,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var asignacion = cuponClienteServicio.obtener(codigo);
        if (asignacion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!principal.esAdministrador()
                && !principal.getCedula().equals(asignacion.get().getCliente().getCedula())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(asignacion.get());
    }

    @GetMapping("/por-cupon")
    @Operation(summary = "Asignación por cupón y cliente", description = "Propio o ADMIN.")
    public ResponseEntity<CuponClienteResponse> obtenerPorCuponYCliente(
            @RequestParam @Positive Integer cupon,
            @RequestParam @Positive Integer cliente,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador() && !principal.getCedula().equals(cliente)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return cuponClienteServicio.obtenerPorCuponYCliente(cupon, cliente)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/redimidos/count")
    @Operation(summary = "Contar cupones redimidos", description = "Propio o ADMIN.")
    public ResponseEntity<Map<String, Long>> contarRedimidos(
            @RequestParam @Positive Integer cliente,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador() && !principal.getCedula().equals(cliente)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(Map.of("redimidos", cuponClienteServicio.contarRedimidosPorCliente(cliente)));
    }

    @GetMapping
    @Operation(summary = "Asignaciones por cliente",
            description = "?cliente= requerido. ?estado=activos|inactivos filtra. Propio o ADMIN.")
    public ResponseEntity<List<CuponClienteResponse>> listarPorCliente(
            @RequestParam @Positive Integer cliente,
            @RequestParam(required = false) String estado,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador() && !principal.getCedula().equals(cliente)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if ("activos".equalsIgnoreCase(estado)) {
            return ResponseEntity.ok(cuponClienteServicio.listarActivosPorCliente(cliente));
        }
        if ("inactivos".equalsIgnoreCase(estado)) {
            return ResponseEntity.ok(cuponClienteServicio.listarInactivosPorCliente(cliente));
        }
        return ResponseEntity.ok(cuponClienteServicio.listarPorCliente(cliente));
    }

    // !SECTION
}
