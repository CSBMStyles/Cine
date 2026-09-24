package com.unicine.test.service.payment;

import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.junit.jupiter.MockitoExtension;

import com.mercadopago.client.order.OrderClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.order.Order;
import com.unicine.entity.purchase.Compra;
import com.unicine.entity.payment.Pago;
import com.unicine.entity.user.Cliente;
import com.unicine.enums.payment.EstadoPago;
import com.unicine.enums.purchase.MedioPago;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ExternalServiceException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.repository.payment.PagoRepo;
import com.unicine.service.payment.PagoServicioImp;
import com.unicine.transfer.dto.request.OrdenPagoRequest;
import com.unicine.transfer.dto.response.OrdenPagoResponse;
import com.unicine.transfer.mapper.PagoMapper;
import com.unicine.util.config.MercadoPagoConfig;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

@ExtendWith(MockitoExtension.class)
public class PagoServicioImpTest {

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

    @InjectMocks
    private PagoServicioImp pagoServicio;

    // La URL base llega por @Value con default, en el test queda el default.
    private Compra compraVigente(Integer codigo) {
        Cliente cliente = org.mockito.Mockito.mock(Cliente.class);
        org.mockito.Mockito.lenient().when(cliente.getCorreo()).thenReturn("comprador@test.com");
        Compra compra = Compra.builder()
                .estado(true)
                .medioPago(MedioPago.NEQUI)
                .cuponCliente(null)
                .cliente(cliente)
                .funcion(null)
                .build();
        compra.setCodigo(codigo);
        compra.setValorTotal(17000.0);
        return compra;
    }

    @Test
    public void crearOrdenGuardaPendiente() throws Exception {
        Compra compra = compraVigente(7);
        when(compraRepo.findById(7)).thenReturn(Optional.of(compra));
        when(pagoRepo.findByCompraCodigo(7)).thenReturn(Optional.empty());
        when(mercadoPagoConfig.getAccessToken()).thenReturn("TEST-xxx");
        Order orden = org.mockito.Mockito.mock(Order.class);
        when(orden.getId()).thenReturn("ORD-1");
        when(orden.getCheckoutUrl()).thenReturn("https://mp/checkout/1");
        when(orderClient.create(any(), any())).thenReturn(orden);
        when(pagoRepo.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));
        OrdenPagoResponse esperado = OrdenPagoResponse.builder()
                .compraCodigo(7).mercadoPagoId("ORD-1")
                .checkoutUrl("https://mp/checkout/1").estado(EstadoPago.PENDIENTE).build();
        when(pagoMapper.toResponse(any(Pago.class))).thenReturn(esperado);

        OrdenPagoResponse respuesta = pagoServicio.crearOrden(
                OrdenPagoRequest.builder().compraCodigo(7).build());

        Assertions.assertEquals("https://mp/checkout/1", respuesta.getCheckoutUrl());
        ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepo).save(captor.capture());
        Assertions.assertEquals(EstadoPago.PENDIENTE, captor.getValue().getEstado());
        Assertions.assertEquals(17000.0, captor.getValue().getMontoEsperado());
        Assertions.assertNotNull(captor.getValue().getIdempotencyKey());
    }

    @Test
    public void crearOrdenCompraInexistente() {
        when(compraRepo.findById(99)).thenReturn(Optional.empty());

        ResourceNotFoundException e = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                pagoServicio.crearOrden(OrdenPagoRequest.builder().compraCodigo(99).build()));

        Assertions.assertEquals(
                PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND.getCode(),
                e.getErrorCode());
    }

    @Test
    public void crearOrdenCompraProcesada() throws Exception {
        Compra compra = compraVigente(5);
        compra.setEstado(false);
        when(compraRepo.findById(5)).thenReturn(Optional.of(compra));

        BusinessRuleException e = Assertions.assertThrows(BusinessRuleException.class, () ->
                pagoServicio.crearOrden(OrdenPagoRequest.builder().compraCodigo(5).build()));

        Assertions.assertEquals(
                PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_PURCHASE_ALREADY_PROCESSED.getCode(),
                e.getErrorCode());
        verify(orderClient, never()).create(any(), any());
    }

    @Test
    public void crearOrdenReintentoNoDuplica() throws Exception {
        Compra compra = compraVigente(7);
        when(compraRepo.findById(7)).thenReturn(Optional.of(compra));
        Pago existente = Pago.builder().compraCodigo(7).mercadoPagoId("ORD-1")
                .checkoutUrl("https://mp/checkout/1").idempotencyKey("k")
                .montoEsperado(17000.0).estado(EstadoPago.PENDIENTE).build();
        when(pagoRepo.findByCompraCodigo(7)).thenReturn(Optional.of(existente));
        OrdenPagoResponse esperado = OrdenPagoResponse.builder()
                .compraCodigo(7).mercadoPagoId("ORD-1")
                .checkoutUrl("https://mp/checkout/1").estado(EstadoPago.PENDIENTE).build();
        when(pagoMapper.toResponse(existente)).thenReturn(esperado);

        OrdenPagoResponse respuesta = pagoServicio.crearOrden(
                OrdenPagoRequest.builder().compraCodigo(7).build());

        Assertions.assertEquals("ORD-1", respuesta.getMercadoPagoId());
        verify(orderClient, never()).create(any(), any());
        verify(pagoRepo, never()).save(any(Pago.class));
    }

    @Test
    public void crearOrdenErrorMercadoPago() throws Exception {
        Compra compra = compraVigente(7);
        when(compraRepo.findById(7)).thenReturn(Optional.of(compra));
        when(pagoRepo.findByCompraCodigo(7)).thenReturn(Optional.empty());
        when(mercadoPagoConfig.getAccessToken()).thenReturn("TEST-xxx");
        MPApiException fallo = org.mockito.Mockito.mock(MPApiException.class);
        when(fallo.getMessage()).thenReturn("mp down");
        when(orderClient.create(any(), any())).thenThrow(fallo);

        ExternalServiceException e = Assertions.assertThrows(ExternalServiceException.class, () ->
                pagoServicio.crearOrden(OrdenPagoRequest.builder().compraCodigo(7).build()));

        Assertions.assertEquals(
                PurchaseErrorCatalog.DOMAIN_PURCHASE_EXTERNAL_ORDER_CREATE_ERROR.getCode(),
                e.getErrorCode());
    }
}
