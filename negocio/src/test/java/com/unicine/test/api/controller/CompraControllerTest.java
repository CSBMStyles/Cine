package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.CompraController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.purchase.CompraServicio;
import com.unicine.transfer.dto.response.CompraResponse;
import com.unicine.util.config.SecurityConfig;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

/**
 * Tests slice para CompraController — checkout transaccional, ownership, idempotencia.
 */
@WebMvcTest(controllers = CompraController.class)
@Import(SecurityConfig.class)
class CompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompraServicio compraServicio;

    private UsuarioPrincipal principalCliente(Integer cedula) {
        return new UsuarioPrincipal(cedula, "pepe@test.com", "hashed", TipoUsuario.CLIENTE);
    }

    private UsuarioPrincipal principalAdmin(Integer cedula) {
        return new UsuarioPrincipal(cedula, "admin@test.com", "hashed", TipoUsuario.ADMINISTRADOR);
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    // SECTION: Checkout completa

    @Test
    void registrarCompraCompleta201() throws Exception {
        CompraResponse mock = CompraResponse.builder().codigo(1).valorTotal(35000.0).build();
        when(compraServicio.registrarCompraCompleta(any())).thenReturn(mock);

        String body = """
                {
                  "compra": {"clienteCedula":1009000011,"funcionCodigo":1,"medioPago":"NEQUI","estado":true,"fechaCompra":"2030-01-01T10:00:00","fechaPelicula":"2030-01-02T18:00:00","valorTotal":999},
                  "entradas": [{"fila":1,"columna":1,"precio":999,"compraCodigo":1,"funcionCodigo":1}],
                  "confiterias": []
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/compras/completas")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(1))
                .andReturn();

        sout("registrarCompraCompleta201 server-side total ignora 999", result);
    }

    @Test
    void registrarCompraCompletaConClienteDistinto403() throws Exception {
        String body = """
                {
                  "compra": {"clienteCedula":999,"funcionCodigo":1,"medioPago":"NEQUI","estado":true,"fechaCompra":"2030-01-01T10:00:00","fechaPelicula":"2030-01-02T18:00:00","valorTotal":35000},
                  "entradas": [],
                  "confiterias": []
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/compras/completas")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();

        sout("registrarCompraCompleta403 cliente distinto", result);
    }

    @Test
    void registrarCompraCompletaSinAuth401() throws Exception {
        String body = """
                {"compra": {"clienteCedula":1009000011,"funcionCodigo":1,"medioPago":"NEQUI","estado":true,"fechaCompra":"2030-01-01T10:00:00","fechaPelicula":"2030-01-02T18:00:00","valorTotal":1},"entradas":[],"confiterias":[]}
                """;

        MvcResult result = mockMvc.perform(post("/api/compras/completas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized())
                .andReturn();

        sout("registrarCompraCompleta401", result);
    }

    // !SECTION
    // SECTION: Historial

    @Test
    void obtenerCompraPropia200() throws Exception {
        CompraResponse mock = CompraResponse.builder().codigo(1).valorTotal(35000.0).build();
        when(compraServicio.obtener(1)).thenReturn(Optional.of(mock));
        when(compraServicio.obtenerComprasCliente(1009000011)).thenReturn(List.of(mock));

        MvcResult result = mockMvc.perform(get("/api/compras/1")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(1))
                .andReturn();

        sout("obtenerCompraPropia200", result);
    }

    @Test
    void obtenerCompraAjena403() throws Exception {
        CompraResponse otra = CompraResponse.builder().codigo(1).valorTotal(35000.0).build();
        when(compraServicio.obtener(1)).thenReturn(Optional.of(otra));
        when(compraServicio.obtenerComprasCliente(1009000011)).thenReturn(List.of());

        MvcResult result = mockMvc.perform(get("/api/compras/1")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isForbidden())
                .andReturn();

        sout("obtenerCompraAjena403", result);
    }

    @Test
    void listarMisCompras200() throws Exception {
        CompraResponse mock = CompraResponse.builder().codigo(1).valorTotal(35000.0).build();
        when(compraServicio.obtenerComprasCliente(1009000011)).thenReturn(List.of(mock));

        MvcResult result = mockMvc.perform(get("/api/compras")
                        .param("cliente", "1009000011")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();

        sout("listarMisCompras200", result);
    }

    @Test
    void listarComprasDeOtro403() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/compras")
                        .param("cliente", "999")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isForbidden())
                .andReturn();

        sout("listarComprasDeOtro403", result);
    }

    @Test
    void idempotenciaCodigoExistente200() throws Exception {
        CompraResponse existente = CompraResponse.builder().codigo(99).valorTotal(10000.0).build();
        when(compraServicio.obtener(99)).thenReturn(Optional.of(existente));

        String body = """
                {"codigo":99,"clienteCedula":1009000011,"funcionCodigo":1,"medioPago":"NEQUI","estado":true,"fechaCompra":"2030-01-01T10:00:00","fechaPelicula":"2030-01-02T18:00:00","valorTotal":1}
                """;

        MvcResult result = mockMvc.perform(post("/api/compras")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(99))
                .andReturn();

        sout("idempotencia99 200 existente", result);
    }

    @Test
    void registrarCompraCompletaReintento200() throws Exception {
        CompraResponse existente = CompraResponse.builder().codigo(7).valorTotal(35000.0).build();
        when(compraServicio.obtener(7)).thenReturn(Optional.of(existente));

        String body = """
                {
                  "compra": {"codigo":7,"clienteCedula":1009000011,"funcionCodigo":1,"medioPago":"NEQUI","estado":true,"fechaCompra":"2030-01-01T10:00:00","fechaPelicula":"2030-01-02T18:00:00","valorTotal":999},
                  "entradas": [{"fila":1,"columna":1,"precio":999,"compraCodigo":7,"funcionCodigo":1}],
                  "confiterias": []
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/compras/completas")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(7))
                .andReturn();

        verify(compraServicio, never()).registrarCompraCompleta(any());
        sout("reintentoCompletas200 sin duplicar", result);
    }

    @Test
    void obtenerSinHistorialPropio403() throws Exception {
        CompraResponse otra = CompraResponse.builder().codigo(5).valorTotal(10000.0).build();
        when(compraServicio.obtener(5)).thenReturn(Optional.of(otra));
        when(compraServicio.obtenerComprasCliente(1009000011))
                .thenThrow(new ResourceNotFoundException(
                        PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND));

        MvcResult result = mockMvc.perform(get("/api/compras/5")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isForbidden())
                .andReturn();

        sout("obtenerSinHistorial403", result);
    }

    @Test
    void listarHistorialVacio200() throws Exception {
        when(compraServicio.obtenerComprasCliente(1009000011))
                .thenThrow(new ResourceNotFoundException(
                        PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND));

        MvcResult result = mockMvc.perform(get("/api/compras")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andReturn();

        sout("historialVacio200", result);
    }

    // !SECTION
}
