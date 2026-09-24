package com.unicine.test.service.payment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.mercadopago.client.order.OrderClient;
import com.mercadopago.exceptions.MPInvalidWebhookSignatureException;
import com.mercadopago.resources.order.Order;
import com.mercadopago.webhook.WebhookSignatureValidator;
import com.unicine.entity.payment.Pago;
import com.unicine.enums.payment.EstadoPago;
import com.unicine.event.payment.PagoConfirmadoEvent;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.repository.payment.PagoRepo;
import com.unicine.service.payment.PagoServicioImp;
import com.unicine.transfer.dto.response.OrdenPagoResponse;
import com.unicine.transfer.mapper.PagoMapper;
import com.unicine.util.config.MercadoPagoConfig;

@ExtendWith(MockitoExtension.class)
public class PagoServicioWebhookTest {

    @Mock
    private PagoRepo pagoRepo;

    @Mock
    private CompraRepo compraRepo;

    @Mock
    private PagoMapper pagoMapper;

    @Mock
    private OrderClient orderClient;

    @Mock
    private MercadoPagoConfig mercadoPagoConfig;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PagoServicioImp pagoServicio;

    private Pago pagoPendiente() {
        return Pago.builder().compraCodigo(7).mercadoPagoId("ORD-1")
                .checkoutUrl("https://mp/checkout/1").idempotencyKey("k")
                .montoEsperado(17000.0).estado(EstadoPago.PENDIENTE).build();
    }

    private Order ordenRemota(String total, String estado) {
        Order orden = org.mockito.Mockito.mock(Order.class);
        org.mockito.Mockito.lenient().when(orden.getTotalAmount()).thenReturn(total);
        org.mockito.Mockito.lenient().when(orden.getStatus()).thenReturn(estado);
        return orden;
    }

    private OrdenPagoResponse respuesta(EstadoPago estado) {
        return OrdenPagoResponse.builder().compraCodigo(7).mercadoPagoId("ORD-1")
                .checkoutUrl("https://mp/checkout/1").estado(estado).build();
    }

    @Test
    public void firmaInvalidaNoTocaNada() throws Exception {
        MPInvalidWebhookSignatureException fallo =
                org.mockito.Mockito.mock(MPInvalidWebhookSignatureException.class);
        when(mercadoPagoConfig.getWebhookSecret()).thenReturn("sec");
        try (MockedStatic<WebhookSignatureValidator> validador =
                mockStatic(WebhookSignatureValidator.class)) {
            validador.when(() -> WebhookSignatureValidator.validate(
                    any(), any(), any(), any())).thenThrow(fallo);

            Assertions.assertThrows(MPInvalidWebhookSignatureException.class, () ->
                    pagoServicio.procesarNotificacion("ORD-1", "order", "falsa", "r1"));
        }
        verify(pagoRepo, never()).findByMercadoPagoId(anyString());
        verify(orderClient, never()).get(anyString(), any());
    }

    @Test
    public void tipoDistintoSeIgnora() throws Exception {
        when(mercadoPagoConfig.getWebhookSecret()).thenReturn("sec");
        try (MockedStatic<WebhookSignatureValidator> validador =
                mockStatic(WebhookSignatureValidator.class)) {
            Optional<OrdenPagoResponse> resultado = pagoServicio.procesarNotificacion(
                    "X", "payment", "firma", "r1");

            Assertions.assertTrue(resultado.isEmpty());
        }
        verify(pagoRepo, never()).findByMercadoPagoId(anyString());
    }

    @Test
    public void ordenAjenaSeIgnora() throws Exception {
        when(mercadoPagoConfig.getWebhookSecret()).thenReturn("sec");
        when(pagoRepo.findByMercadoPagoId("ORD-X")).thenReturn(Optional.empty());
        try (MockedStatic<WebhookSignatureValidator> validador =
                mockStatic(WebhookSignatureValidator.class)) {
            Optional<OrdenPagoResponse> resultado = pagoServicio.procesarNotificacion(
                    "ORD-X", "order", "firma", "r1");

            Assertions.assertTrue(resultado.isEmpty());
        }
        verify(orderClient, never()).get(anyString(), any());
    }

    @Test
    public void aprobadaConfirmaYPublicaEvento() throws Exception {
        Pago pago = pagoPendiente();
        when(mercadoPagoConfig.getWebhookSecret()).thenReturn("sec");
        when(mercadoPagoConfig.getAccessToken()).thenReturn("TEST-xxx");
        when(pagoRepo.findByMercadoPagoId("ORD-1")).thenReturn(Optional.of(pago));
        Order ordenAprobada = ordenRemota("17000.00", "approved");
        when(orderClient.get(anyString(), any())).thenReturn(ordenAprobada);
        when(pagoRepo.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));
        when(pagoMapper.toResponse(any(Pago.class))).thenReturn(respuesta(EstadoPago.PAGADA));
        try (MockedStatic<WebhookSignatureValidator> validador =
                mockStatic(WebhookSignatureValidator.class)) {
            Optional<OrdenPagoResponse> resultado = pagoServicio.procesarNotificacion(
                    "ORD-1", "order", "firma", "r1");

            Assertions.assertTrue(resultado.isPresent());
            Assertions.assertEquals(EstadoPago.PAGADA, resultado.get().getEstado());
        }
        ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepo).save(captor.capture());
        Assertions.assertEquals(EstadoPago.PAGADA, captor.getValue().getEstado());
        ArgumentCaptor<PagoConfirmadoEvent> evento = ArgumentCaptor.forClass(PagoConfirmadoEvent.class);
        verify(eventPublisher).publishEvent(evento.capture());
        Assertions.assertEquals(7, evento.getValue().compraCodigo());
    }

    @Test
    public void redeliveryNoDuplicaNiPublica() throws Exception {
        Pago pago = pagoPendiente();
        pago.setEstado(EstadoPago.PAGADA);
        when(mercadoPagoConfig.getWebhookSecret()).thenReturn("sec");
        when(mercadoPagoConfig.getAccessToken()).thenReturn("TEST-xxx");
        when(pagoRepo.findByMercadoPagoId("ORD-1")).thenReturn(Optional.of(pago));
        Order ordenAprobada = ordenRemota("17000.00", "approved");
        when(orderClient.get(anyString(), any())).thenReturn(ordenAprobada);
        when(pagoMapper.toResponse(pago)).thenReturn(respuesta(EstadoPago.PAGADA));
        try (MockedStatic<WebhookSignatureValidator> validador =
                mockStatic(WebhookSignatureValidator.class)) {
            Optional<OrdenPagoResponse> resultado = pagoServicio.procesarNotificacion(
                    "ORD-1", "order", "firma", "r1");

            Assertions.assertTrue(resultado.isPresent());
        }
        verify(pagoRepo, never()).save(any(Pago.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    public void montoDistintoNoConfirma() throws Exception {
        Pago pago = pagoPendiente();
        when(mercadoPagoConfig.getWebhookSecret()).thenReturn("sec");
        when(mercadoPagoConfig.getAccessToken()).thenReturn("TEST-xxx");
        when(pagoRepo.findByMercadoPagoId("ORD-1")).thenReturn(Optional.of(pago));
        Order ordenMontoDistinto = ordenRemota("999.00", "approved");
        when(orderClient.get(anyString(), any())).thenReturn(ordenMontoDistinto);
        when(pagoMapper.toResponse(pago)).thenReturn(respuesta(EstadoPago.PENDIENTE));
        try (MockedStatic<WebhookSignatureValidator> validador =
                mockStatic(WebhookSignatureValidator.class)) {
            Optional<OrdenPagoResponse> resultado = pagoServicio.procesarNotificacion(
                    "ORD-1", "order", "firma", "r1");

            Assertions.assertTrue(resultado.isPresent());
            Assertions.assertEquals(EstadoPago.PENDIENTE, resultado.get().getEstado());
        }
        verify(pagoRepo, never()).save(any(Pago.class));
    }
}
