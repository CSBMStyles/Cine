package com.unicine.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unicine.exception.ResourceNotFoundException;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.payment.PagoServicio;
import com.unicine.service.purchase.CompraServicio;
import com.unicine.transfer.dto.request.OrdenPagoRequest;
import com.unicine.transfer.dto.response.CompraResponse;
import com.unicine.transfer.dto.response.OrdenPagoResponse;
import com.unicine.transfer.mapper.PagoMapper;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller de pagos — orden Checkout Pro sobre compras registradas.
 * Monto y pagador salen del servidor; valida ownership via principal.cedula.
 */
@RestController
@RequestMapping("/api/pagos")
@Validated
@Tag(name = "Pagos", description = "Orden Checkout Pro y estado de pagos")
public class PagoController {

    private final PagoServicio pagoServicio;

    private final CompraServicio compraServicio;

    private final PagoMapper pagoMapper;

    public PagoController(PagoServicio pagoServicio, CompraServicio compraServicio, PagoMapper pagoMapper) {
        this.pagoServicio = pagoServicio;
        this.compraServicio = compraServicio;
        this.pagoMapper = pagoMapper;
    }

    // SECTION: Ordenes

    @PostMapping("/ordenes")
    @Operation(summary = "Crear orden de pago",
            description = "Crea la orden Checkout Pro (Orders API) desde una compra registrada. "
                    + "Monto server-side desde Compra.valorTotal. Idempotente por compra: "
                    + "reintento devuelve 200 con la existente.")
    public ResponseEntity<OrdenPagoResponse> crearOrden(
            @Valid @RequestBody OrdenPagoRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CompraResponse compra = compraServicio.obtener(request.getCompraCodigo())
                .orElseThrow(() -> new ResourceNotFoundException(
                        PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND));
        if (!principal.getCedula().equals(compra.getCliente().getCedula())) {
            if (!principal.esAdministrador()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        // Idempotencia: si la compra ya tiene pago, devolverlo con 200
        var existente = pagoServicio.obtenerPorCompra(request.getCompraCodigo());
        if (existente.isPresent()) {
            return ResponseEntity.ok(pagoMapper.toResponse(existente.get()));
        }
        OrdenPagoResponse response = pagoServicio.crearOrden(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // !SECTION
}
