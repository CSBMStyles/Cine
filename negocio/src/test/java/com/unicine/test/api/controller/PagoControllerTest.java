package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.PagoController;
import com.unicine.entity.payment.Pago;
import com.unicine.enums.payment.EstadoPago;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.payment.PagoServicio;
import com.unicine.service.purchase.CompraServicio;
import com.unicine.transfer.dto.response.ClienteResponse;
import com.unicine.transfer.dto.response.CompraResponse;
import com.unicine.transfer.dto.response.OrdenPagoResponse;
import com.unicine.transfer.mapper.PagoMapper;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para PagoController — orden Checkout Pro, ownership, idempotencia.
 */
@WebMvcTest(controllers = PagoController.class)
@Import(SecurityConfig.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagoServicio pagoServicio;

    @MockitoBean
    private CompraServicio compraServicio;

    @MockitoBean
    private PagoMapper pagoMapper;

    private UsuarioPrincipal principalCliente(Integer cedula) {
        return new UsuarioPrincipal(cedula, "pepe@test.com", "hashed", TipoUsuario.CLIENTE);
    }

    private CompraResponse compraPropia(Integer codigo, Integer cedula) {
        ClienteResponse cliente = ClienteResponse.builder().cedula(cedula).build();
        return CompraResponse.builder().codigo(codigo).cliente(cliente).valorTotal(17000.0).build();
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    // SECTION: Ordenes

    @Test
    void crearOrden201() throws Exception {
        when(compraServicio.obtener(7)).thenReturn(Optional.of(compraPropia(7, 1009000011)));
        when(pagoServicio.obtenerPorCompra(7)).thenReturn(Optional.empty());
        OrdenPagoResponse mock = OrdenPagoResponse.builder().compraCodigo(7)
                .mercadoPagoId("ORD-1").checkoutUrl("https://mp/checkout/1")
                .estado(EstadoPago.PENDIENTE).build();
        when(pagoServicio.crearOrden(any())).thenReturn(mock);

        MvcResult result = mockMvc.perform(post("/api/pagos/ordenes")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compraCodigo\":7}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.checkoutUrl").value("https://mp/checkout/1"))
                .andReturn();

        sout("crearOrden201", result);
    }

    @Test
    void crearOrdenReintento200() throws Exception {
        when(compraServicio.obtener(7)).thenReturn(Optional.of(compraPropia(7, 1009000011)));
        Pago existente = Pago.builder().compraCodigo(7).mercadoPagoId("ORD-1")
                .checkoutUrl("https://mp/checkout/1").idempotencyKey("k")
                .montoEsperado(17000.0).estado(EstadoPago.PENDIENTE).build();
        when(pagoServicio.obtenerPorCompra(7)).thenReturn(Optional.of(existente));
        OrdenPagoResponse mock = OrdenPagoResponse.builder().compraCodigo(7)
                .mercadoPagoId("ORD-1").checkoutUrl("https://mp/checkout/1")
                .estado(EstadoPago.PENDIENTE).build();
        when(pagoMapper.toResponse(existente)).thenReturn(mock);

        MvcResult result = mockMvc.perform(post("/api/pagos/ordenes")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compraCodigo\":7}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mercadoPagoId").value("ORD-1"))
                .andReturn();

        verify(pagoServicio, never()).crearOrden(any());
        sout("crearOrdenReintento200", result);
    }

    @Test
    void crearOrdenCompraAjena403() throws Exception {
        when(compraServicio.obtener(7)).thenReturn(Optional.of(compraPropia(7, 999)));

        MvcResult result = mockMvc.perform(post("/api/pagos/ordenes")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compraCodigo\":7}"))
                .andExpect(status().isForbidden())
                .andReturn();

        sout("crearOrden403 compra ajena", result);
    }

    @Test
    void crearOrdenSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/pagos/ordenes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compraCodigo\":7}"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        sout("crearOrden401", result);
    }

    @Test
    void crearOrdenCompraInexistente404() throws Exception {
        when(compraServicio.obtener(99)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(post("/api/pagos/ordenes")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compraCodigo\":99}"))
                .andExpect(status().isNotFound())
                .andReturn();

        sout("crearOrden404", result);
    }

    // !SECTION
    // SECTION: Webhooks

    @Test
    void webhookFirmaInvalida401() throws Exception {
        com.mercadopago.exceptions.MPInvalidWebhookSignatureException fallo =
                org.mockito.Mockito.mock(com.mercadopago.exceptions.MPInvalidWebhookSignatureException.class);
        when(pagoServicio.procesarNotificacion(any(), any(), any(), any())).thenThrow(fallo);

        MvcResult result = mockMvc.perform(post("/api/pagos/webhooks")
                        .queryParam("data.id", "ORD-1")
                        .queryParam("type", "order")
                        .header("x-signature", "falsa")
                        .header("x-request-id", "r1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();

        sout("webhook401 firma falsa", result);
    }

    @Test
    void webhookValido200() throws Exception {
        OrdenPagoResponse mock = OrdenPagoResponse.builder().compraCodigo(7)
                .mercadoPagoId("ORD-1").checkoutUrl("https://mp/checkout/1")
                .estado(com.unicine.enums.payment.EstadoPago.PAGADA).build();
        when(pagoServicio.procesarNotificacion(any(), any(), any(), any()))
                .thenReturn(Optional.of(mock));

        MvcResult result = mockMvc.perform(post("/api/pagos/webhooks")
                        .queryParam("data.id", "ORD-1")
                        .queryParam("type", "order")
                        .header("x-signature", "ts=1,v1=abc")
                        .header("x-request-id", "r1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        sout("webhook200", result);
    }

    // !SECTION
}
