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
import com.mercadopago.webhook.WebhookSignatureValidator;
import com.unicine.entity.purchase.Compra;
import com.unicine.entity.payment.Pago;
import com.unicine.enums.payment.EstadoPago;
import com.unicine.event.payment.PagoConfirmadoEvent;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// Adaptador de Mercado Pago detras del puerto PagoServicio: el monto y el
// pagador salen de la compra registrada, nunca del body del cliente.
@Service
@Validated
@Slf4j
public class PagoServicioImp implements PagoServicio {

    // SECTION: Atributos e inyeccion

    private final PagoRepo pagoRepo;

    private final CompraRepo compraRepo;

    private final PagoMapper pagoMapper;

    private final OrderClient orderClient;

    private final MercadoPagoConfig mercadoPagoConfig;

    private final ApplicationEventPublisher eventPublisher;

    private final String baseUrl;

    public PagoServicioImp(PagoRepo pagoRepo, CompraRepo compraRepo, PagoMapper pagoMapper,
                           OrderClient orderClient, MercadoPagoConfig mercadoPagoConfig,
                           ApplicationEventPublisher eventPublisher,
                           @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.pagoRepo = pagoRepo;
        this.compraRepo = compraRepo;
        this.pagoMapper = pagoMapper;
        this.orderClient = orderClient;
        this.mercadoPagoConfig = mercadoPagoConfig;
        this.eventPublisher = eventPublisher;
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

    // Mapea el status de la orden al estado local. Valores por doc MP a
    // verificar en sandbox 4.7.5: approved confirma, el resto espera o falla.
    private EstadoPago mapearEstado(String statusOrden) {
        if (statusOrden == null) {
            return EstadoPago.EN_VERIFICACION;
        }
        return switch (statusOrden.toLowerCase(Locale.ROOT)) {
            case "approved", "authorized" -> EstadoPago.PAGADA;
            case "expired" -> EstadoPago.EXPIRADA;
            case "cancelled", "canceled", "rejected", "refunded", "charged_back" -> EstadoPago.FALLIDA;
            default -> EstadoPago.EN_VERIFICACION;
        };
    }

    private boolean puedeAvanzar(EstadoPago actual, EstadoPago destino) {
        return actual.puedePasarA(destino)
                || (actual == EstadoPago.PENDIENTE && destino == EstadoPago.PAGADA);
    }

    // Lecturas remotas con retry topado y explicito: 3 intentos con backoff
    // solo ante 429 y 5xx. Las escrituras nunca reintentan.
    private MPRequestOptions opcionesLectura() {
        return MPRequestOptions.builder()
                .accessToken(exigirToken())
                .maxRetries(3)
                .retryOn(List.of(429, 502, 503, 504))
                .initialDelayMs(500L)
                .maxDelayMs(4000L)
                .build();
    }

    private Order obtenerOrdenRemota(String mercadoPagoId) {
        try {
            return orderClient.get(mercadoPagoId, opcionesLectura());
        } catch (MPApiException | MPException e) {
            throw new ExternalServiceException(
                    PurchaseErrorCatalog.DOMAIN_PURCHASE_EXTERNAL_ORDER_CREATE_ERROR, e.getMessage());
        }
    }

    private boolean esFinal(EstadoPago estado) {
        return estado == EstadoPago.PAGADA
                || estado == EstadoPago.FALLIDA
                || estado == EstadoPago.EXPIRADA;
    }

    // Núcleo compartido por webhook y reconciliacion: lee la verdad remota,
    // compara montos, avanza con guardia y publica el evento al confirmar.
    private OrdenPagoResponse sincronizarConOrden(Pago pago) {
        Order orden = obtenerOrdenRemota(pago.getMercadoPagoId());
        String totalOrden = orden.getTotalAmount();
        String totalEsperado = String.format(Locale.ROOT, "%.2f", pago.getMontoEsperado());
        if (totalOrden != null && !totalOrden.equals(totalEsperado)) {
            log.warn("Monto {} distinto al esperado {} en pago {}", totalOrden, totalEsperado, pago.getCodigo());
            return pagoMapper.toResponse(pago);
        }
        EstadoPago destino = mapearEstado(orden.getStatus());
        if (pago.getEstado() == destino || !puedeAvanzar(pago.getEstado(), destino)) {
            return pagoMapper.toResponse(pago);
        }
        pago.setEstado(destino);
        pago.setFechaActualizacion(LocalDateTime.now(ZoneId.of("America/Bogota")));
        Pago guardado = pagoRepo.save(pago);
        if (destino == EstadoPago.PAGADA) {
            eventPublisher.publishEvent(new PagoConfirmadoEvent(
                    guardado.getCompraCodigo(), guardado.getMercadoPagoId(),
                    guardado.getMontoEsperado(), guardado.getFechaActualizacion()));
        }
        return pagoMapper.toResponse(guardado);
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

    // Firma primero: si es falsa no se toca base ni red. Tipo distinto u
    // orden ajena se ignora con 200 para que MP no reintente en vano. Monto
    // distinto no confirma: se queda en verificacion para 4.7.4.
    @Override
    @Transactional
    public Optional<OrdenPagoResponse> procesarNotificacion(String dataId, String tipo,
                                                            String firma, String requestId) throws Exception {
        // Secreto en crudo: sin secreto nada valida y todo se rechaza con 401.
        WebhookSignatureValidator.validate(firma, requestId, dataId,
                mercadoPagoConfig.getWebhookSecret());
        if (!"order".equalsIgnoreCase(tipo)) {
            return Optional.empty();
        }
        Optional<Pago> buscado = pagoRepo.findByMercadoPagoId(dataId);
        if (buscado.isEmpty()) {
            log.warn("Webhook de orden ajena {} ignorado", dataId);
            return Optional.empty();
        }
        return Optional.of(sincronizarConOrden(buscado.get()));
    }

    // Estado final se devuelve sin red: la red no puede cambiarlo.
    @Override
    @Transactional
    public OrdenPagoResponse conciliarEstado(Integer compraCodigo) {
        Pago pago = pagoRepo.findByCompraCodigo(compraCodigo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PAYMENT_NOT_FOUND));
        if (esFinal(pago.getEstado())) {
            return pagoMapper.toResponse(pago);
        }
        return sincronizarConOrden(pago);
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
