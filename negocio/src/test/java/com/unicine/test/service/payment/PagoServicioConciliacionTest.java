package com.unicine.test.service.payment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.mercadopago.client.order.OrderClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.order.Order;
import com.unicine.entity.payment.Pago;
import com.unicine.enums.payment.EstadoPago;
import com.unicine.event.payment.PagoConfirmadoEvent;
import com.unicine.exception.ExternalServiceException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.repository.payment.PagoRepo;
import com.unicine.service.payment.PagoServicioImp;
import com.unicine.transfer.dto.response.OrdenPagoResponse;
import com.unicine.transfer.mapper.PagoMapper;
import com.unicine.util.config.MercadoPagoConfig;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

@ExtendWith(MockitoExtension.class)
public class PagoServicioConciliacionTest {

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

    private Pago pagoEn(EstadoPago estado) {
        return Pago.builder().compraCodigo(7).mercadoPagoId("ORD-1")
                .checkoutUrl("https://mp/checkout/1").idempotencyKey("k")
                .montoEsperado(17000.0).estado(estado).build();
    }

    private Order ordenRemota(String total, String estado) {
        Order orden = org.mockito.Mockito.mock(Order.class);
        org.mockito.Mockito.lenient().when(orden.getTotalAmount()).thenReturn(total);
        org.mockito.Mockito.lenient().when(orden.getStatus()).thenReturn(estado);
        return orden;
    }

    @Test
    public void finalNoLlamaRed() throws Exception {
        Pago pago = pagoEn(EstadoPago.PAGADA);
        when(pagoRepo.findByCompraCodigo(7)).thenReturn(Optional.of(pago));
        OrdenPagoResponse esperado = OrdenPagoResponse.builder().compraCodigo(7)
                .mercadoPagoId("ORD-1").checkoutUrl("https://mp/checkout/1")
                .estado(EstadoPago.PAGADA).build();
        when(pagoMapper.toResponse(pago)).thenReturn(esperado);

        OrdenPagoResponse respuesta = pagoServicio.conciliarEstado(7);

        Assertions.assertEquals(EstadoPago.PAGADA, respuesta.getEstado());
        verify(orderClient, never()).get(anyString(), any());
    }

    @Test
    public void pendienteSincronizaYapublica() throws Exception {
        Pago pago = pagoEn(EstadoPago.PENDIENTE);
        when(pagoRepo.findByCompraCodigo(7)).thenReturn(Optional.of(pago));
        when(mercadoPagoConfig.getAccessToken()).thenReturn("TEST-xxx");
        Order ordenAprobada = ordenRemota("17000.00", "approved");
        when(orderClient.get(anyString(), any())).thenReturn(ordenAprobada);
        when(pagoRepo.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));
        OrdenPagoResponse esperado = OrdenPagoResponse.builder().compraCodigo(7)
                .mercadoPagoId("ORD-1").checkoutUrl("https://mp/checkout/1")
                .estado(EstadoPago.PAGADA).build();
        when(pagoMapper.toResponse(any(Pago.class))).thenReturn(esperado);

        OrdenPagoResponse respuesta = pagoServicio.conciliarEstado(7);

        Assertions.assertEquals(EstadoPago.PAGADA, respuesta.getEstado());
        verify(eventPublisher).publishEvent(any(PagoConfirmadoEvent.class));
    }

    @Test
    public void falloRemotoEs502() throws Exception {
        Pago pago = pagoEn(EstadoPago.PENDIENTE);
        when(pagoRepo.findByCompraCodigo(7)).thenReturn(Optional.of(pago));
        when(mercadoPagoConfig.getAccessToken()).thenReturn("TEST-xxx");
        MPApiException fallo = org.mockito.Mockito.mock(MPApiException.class);
        when(fallo.getMessage()).thenReturn("mp down");
        when(orderClient.get(anyString(), any())).thenThrow(fallo);

        ExternalServiceException e = Assertions.assertThrows(ExternalServiceException.class, () ->
                pagoServicio.conciliarEstado(7));

        Assertions.assertEquals(
                PurchaseErrorCatalog.DOMAIN_PURCHASE_EXTERNAL_ORDER_CREATE_ERROR.getCode(),
                e.getErrorCode());
    }

    @Test
    public void sinPagoEs404() {
        when(pagoRepo.findByCompraCodigo(99)).thenReturn(Optional.empty());

        ResourceNotFoundException e = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                pagoServicio.conciliarEstado(99));

        Assertions.assertEquals(
                PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PAYMENT_NOT_FOUND.getCode(),
                e.getErrorCode());
    }
}
