package com.unicine.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unicine.exception.ResourceNotFoundException;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.purchase.CompraServicio;
import com.unicine.transfer.dto.request.CompraCompletaRequest;
import com.unicine.transfer.dto.request.CompraRequest;
import com.unicine.transfer.dto.response.CompraResponse;
import com.unicine.util.pagination.PaginadoManual;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller de compras — checkout transaccional + historial.
 * Valida ownership via principal.cedula y calcula total server-side.
 */
@RestController
@RequestMapping("/api/compras")
@Validated
@Tag(name = "Compras", description = "Checkout y historial de compras")
public class CompraController {

    private final CompraServicio compraServicio;

    public CompraController(CompraServicio compraServicio) {
        this.compraServicio = compraServicio;
    }

    // SECTION: Checkout

    @PostMapping
    @Operation(summary = "Registrar compra simple", description = "Ignora valorTotal del body — se calcula server-side. Valida cliente == principal.")
    public ResponseEntity<CompraResponse> registrar(
            @Valid @RequestBody CompraRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.getCedula().equals(request.getClienteCedula())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Idempotencia: si codigo ya existe, devolver existente
        if (request.getCodigo() != null) {
            var existente = compraServicio.obtener(request.getCodigo());
            if (existente.isPresent()) {
                return ResponseEntity.ok(existente.get());
            }
        }
        CompraResponse response = compraServicio.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/completas")
    @Operation(summary = "Registrar compra completa", description = "Transacción: compra + entradas + confitería + cupón. Ignora precios/valorTotal del body, sillas atómicas. Idempotente por codigo: reintento devuelve 200 con la existente.")
    public ResponseEntity<CompraResponse> registrarCompleta(
            @Valid @RequestBody CompraCompletaRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Integer clienteCedula = request.getCompra().getClienteCedula();
        if (!principal.getCedula().equals(clienteCedula)) {
            if (!principal.esAdministrador()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        // Idempotencia: si codigo ya existe, devolver existente con 200
        if (request.getCompra().getCodigo() != null) {
            var existente = compraServicio.obtener(request.getCompra().getCodigo());
            if (existente.isPresent()) {
                return ResponseEntity.ok(existente.get());
            }
        }
        CompraResponse response = compraServicio.registrarCompraCompleta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // !SECTION
    // SECTION: Lectura

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener compra por código", description = "Solo owner o ADMIN")
    public ResponseEntity<CompraResponse> obtener(
            @PathVariable @Positive Integer codigo,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var compra = compraServicio.obtener(codigo);
        if (compra.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Ownership: solo ADMIN o compras propias. Sin historial propio -> 403.
        if (!principal.esAdministrador()) {
            List<CompraResponse> misCompras;
            try {
                misCompras = compraServicio.obtenerComprasCliente(principal.getCedula());
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            boolean esMia = misCompras.stream().anyMatch(c -> codigo.equals(c.getCodigo()));
            if (!esMia) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok(compra.get());
    }

    @GetMapping
    @Operation(summary = "Listar compras", description = "Filtros: ?cliente= (solo propio o ADMIN), ?page=&size=. Vacío → 200 [].")
    public ResponseEntity<List<CompraResponse>> listar(
            @RequestParam(required = false) Integer cliente,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean esAdmin = principal.esAdministrador();

        if (cliente != null) {
            if (!esAdmin && !principal.getCedula().equals(cliente)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(compraServicio.obtenerComprasCliente(cliente));
        }

        // Sin filtro cliente: si es admin lista todo, si no solo suyas (vacío → 200 []).
        if (esAdmin) {
            List<CompraResponse> todas = compraServicio.listar();
            return ResponseEntity.ok(PaginadoManual.paginar(todas, page, size));
        } else {
            try {
                List<CompraResponse> mias = compraServicio.obtenerComprasCliente(principal.getCedula());
                return ResponseEntity.ok(PaginadoManual.paginar(mias, page, size));
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.ok(List.of());
            }
        }
    }

    // !SECTION
}
