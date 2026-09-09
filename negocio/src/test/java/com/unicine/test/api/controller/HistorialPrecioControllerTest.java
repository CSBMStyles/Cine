package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
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

import com.unicine.api.controller.HistorialPrecioController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.confiteria.HistorialPrecioPresentacionServicio;
import com.unicine.transfer.dto.response.HistorialPrecioPresentacionResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para HistorialPrecioController — permisos por rol.
 */
@WebMvcTest(controllers = HistorialPrecioController.class)
@Import(SecurityConfig.class)
class HistorialPrecioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HistorialPrecioPresentacionServicio historialServicio;

    private UsuarioPrincipal principalAdmin() {
        return new UsuarioPrincipal(2001, "admin@test.com", "hash", TipoUsuario.ADMINISTRADOR);
    }

    private UsuarioPrincipal principalCliente() {
        return new UsuarioPrincipal(1009000011, "pepe@test.com", "hash", TipoUsuario.CLIENTE);
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    void ultimoComoCliente200() throws Exception {
        HistorialPrecioPresentacionResponse mock = HistorialPrecioPresentacionResponse.builder()
                .codigo(1).porcentaje(20).build();
        when(historialServicio.obtenerUltimoPorPresentacion(5)).thenReturn(Optional.of(mock));

        MvcResult result = mockMvc.perform(get("/api/historial-precios/ultimo")
                        .param("presentacion", "5")
                        .with(user(principalCliente())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(1))
                .andReturn();
        sout("ultimoCliente200", result);
    }

    @Test
    void ultimoSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/historial-precios/ultimo")
                        .param("presentacion", "5"))
                .andExpect(status().isUnauthorized())
                .andReturn();
        sout("ultimo401", result);
    }

    @Test
    void completoComoCliente403() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/historial-precios")
                        .param("presentacion", "5")
                        .with(user(principalCliente())))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("completoCliente403", result);
    }

    @Test
    void completoComoAdmin200() throws Exception {
        when(historialServicio.listarPorPresentacion(5)).thenReturn(List.of(
                HistorialPrecioPresentacionResponse.builder().codigo(1).build()));

        MvcResult result = mockMvc.perform(get("/api/historial-precios")
                        .param("presentacion", "5")
                        .with(user(principalAdmin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("completoAdmin200", result);
    }

    @Test
    void registrarComoAdmin201() throws Exception {
        HistorialPrecioPresentacionResponse mock = HistorialPrecioPresentacionResponse.builder().codigo(1).build();
        when(historialServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"precioAnterior":10000.0,"precioNuevo":8000.0,"tipoCambio":"DESCUENTO_TEMPORAL","porcentaje":20,"fechaCambio":"2030-01-01T10:00:00","presentacionCodigo":5}
                """;

        MvcResult result = mockMvc.perform(post("/api/historial-precios")
                        .with(user(principalAdmin()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(1))
                .andReturn();
        sout("registrarAdmin201", result);
    }

    @Test
    void registrarComoCliente403() throws Exception {
        String body = """
                {"precioAnterior":10000.0,"precioNuevo":8000.0,"tipoCambio":"DESCUENTO_TEMPORAL","porcentaje":20,"fechaCambio":"2030-01-01T10:00:00","presentacionCodigo":5}
                """;

        MvcResult result = mockMvc.perform(post("/api/historial-precios")
                        .with(user(principalCliente()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("registrarCliente403", result);
    }
}
