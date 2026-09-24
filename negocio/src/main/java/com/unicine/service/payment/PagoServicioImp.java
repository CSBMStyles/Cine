package com.unicine.service.payment;

import com.mercadopago.client.order.OrderClient;
import com.mercadopago.client.order.OrderConfigRequest;
import com.mercadopago.client.order.OrderCreateRequest;
import com.mercadopago.client.order.OrderItemRequest;
import com.mercadopago.client.order.OrderOnlineConfig;
import com.mercadopago.client.order.OrderPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.order.Order;
import com.unicine.entity.purchase.Compra;
import com.unicine.entity.payment.Pago;
import com.unicine.enums.payment.EstadoPago;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ExternalServiceException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.repository.payment.PagoRepo;
import com.unicine.transfer.dto.request.OrdenPagoRequest;
import com.unicine.transfer.dto.response.OrdenPagoResponse;
import com.unicine.transfer.mapper.PagoMapper;
import com.unicine.util.config.MercadoPagoConfig;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// Adaptador de Mercado Pago detras del puerto PagoServicio: el monto y el
// pagador salen de la compra registrada, nunca del body del cliente.
@Service
@Validated
public class PagoServicioImp implements PagoServicio {

    // SECTION: Atributos e inyeccion

    private final PagoRepo pagoRepo;

    private final CompraRepo compraRepo;

    private final PagoMapper pagoMapper;

    private final OrderClient orderClient;

    private final MercadoPagoConfig mercadoPagoConfig;

    private final String baseUrl;

    public PagoServicioImp(PagoRepo pagoRepo, CompraRepo compraRepo, PagoMapper pagoMapper,
                           OrderClient orderClient, MercadoPagoConfig mercadoPagoConfig,
                           @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.pagoRepo = pagoRepo;
        this.compraRepo = compraRepo;
        this.pagoMapper = pagoMapper;
        this.orderClient = orderClient;
        this.mercadoPagoConfig = mercadoPagoConfig;
        this.baseUrl = baseUrl;
    }

    // !SECTION
    // SECTION: Metodos de soporte

    private Compra obtenerCompraVigente(Integer compraCodigo) {
        Compra compra = compraRepo.findById(compraCodigo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND));
        if (!compra.getEstado()) {
            throw new BusinessRuleException(
                    PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_PURCHASE_ALREADY_PROCESSED);
        }
        return compra;
    }

    private String exigirToken() {
        String token = mercadoPagoConfig.getAccessToken();
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("La propiedad MERCADOPAGO_ACCESS_TOKEN es obligatoria");
        }
        return token;
    }

    private OrderCreateRequest construirOrden(Compra compra) {
        String total = String.format(Locale.ROOT, "%.2f", compra.getValorTotal());
        OrderItemRequest item = OrderItemRequest.builder()
                .title("Compra UniCine #" + compra.getCodigo())
                .unitPrice(total)
                .quantity(1)
                .build();
        OrderOnlineConfig retorno = OrderOnlineConfig.builder()
                .successUrl(baseUrl + "/pago/exito")
                .failureUrl(baseUrl + "/pago/fallo")
                .pendingUrl(baseUrl + "/pago/pendiente")
                .build();
        return OrderCreateRequest.builder()
                .type("online")
                .processingMode("manual")
                .totalAmount(total)
                .externalReference(String.valueOf(compra.getCodigo()))
                .payer(OrderPayerRequest.builder()
                        .email(compra.getCliente().getCorreo())
                        .build())
                .items(List.of(item))
                .config(OrderConfigRequest.builder()
                        .statementDescriptor("UNICINE")
                        .online(retorno)
                        .build())
                .build();
    }

    private Order crearOrdenRemota(OrderCreateRequest solicitud, String claveIdempotencia) {
        MPRequestOptions opciones = MPRequestOptions.builder()
                .accessToken(exigirToken())
                .customHeaders(Map.of("X-Idempotency-Key", claveIdempotencia))
                .build();
        try {
            return orderClient.create(solicitud, opciones);
        } catch (MPApiException | MPException e) {
            throw new ExternalServiceException(
                    PurchaseErrorCatalog.DOMAIN_PURCHASE_EXTERNAL_ORDER_CREATE_ERROR, e.getMessage());
        }
    }

    private Pago guardarPendiente(Compra compra, String claveIdempotencia, Order orden) {
        try {
            return pagoRepo.save(Pago.builder()
                    .compraCodigo(compra.getCodigo())
                    .mercadoPagoId(orden.getId())
                    .checkoutUrl(orden.getCheckoutUrl())
                    .idempotencyKey(claveIdempotencia)
                    .montoEsperado(compra.getValorTotal())
                    .estado(EstadoPago.PENDIENTE)
                    .build());
        } catch (DataIntegrityViolationException e) {
            return pagoRepo.findByCompraCodigo(compra.getCodigo())
                    .orElseThrow(() -> e);
        }
    }

    // !SECTION
    // SECTION: Metodos de negocio

    @Override
    @Transactional
    public Pago registrarPagoPendiente(Integer compraCodigo, Double montoEsperado) {
        return pagoRepo.findByCompraCodigo(compraCodigo)
                .orElseGet(() -> pagoRepo.save(Pago.builder()
                        .compraCodigo(compraCodigo)
                        .idempotencyKey(UUID.randomUUID().toString())
                        .montoEsperado(montoEsperado)
                        .estado(EstadoPago.PENDIENTE)
                        .build()));
    }

    @Override
    @Transactional
    public OrdenPagoResponse crearOrden(OrdenPagoRequest request) {
        Compra compra = obtenerCompraVigente(request.getCompraCodigo());
        Optional<Pago> existente = pagoRepo.findByCompraCodigo(compra.getCodigo());
        if (existente.isPresent()) {
            return pagoMapper.toResponse(existente.get());
        }
        String claveIdempotencia = UUID.randomUUID().toString();
        Order orden = crearOrdenRemota(construirOrden(compra), claveIdempotencia);
        return pagoMapper.toResponse(guardarPendiente(compra, claveIdempotencia, orden));
    }

    // !SECTION
    // SECTION: Metodos de listado

    @Override
    @Transactional(readOnly = true)
    public Optional<Pago> obtenerPorCompra(Integer compraCodigo) {
        return pagoRepo.findByCompraCodigo(compraCodigo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> listar() {
        return pagoRepo.findAll();
    }

    // !SECTION
}
