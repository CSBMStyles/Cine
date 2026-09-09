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

import com.unicine.api.controller.PresentacionController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.confiteria.ConfiteriaPresentacionServicio;
import com.unicine.transfer.dto.response.ConfiteriaPresentacionResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para PresentacionController — catalogo publico + precios protegidos.
 */
@WebMvcTest(controllers = PresentacionController.class)
@Import(SecurityConfig.class)
class PresentacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConfiteriaPresentacionServicio presentacionServicio;

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
    void listarPublico200() throws Exception {
        when(presentacionServicio.listar()).thenReturn(List.of(
                ConfiteriaPresentacionResponse.builder().codigo(1).precio(10000.0).build()));

        MvcResult result = mockMvc.perform(get("/api/presentaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("listarPublico200", result);
    }

    @Test
    void conDescuentoPublico200() throws Exception {
        when(presentacionServicio.listarConDescuentoTemporal()).thenReturn(List.of(
                ConfiteriaPresentacionResponse.builder().codigo(2).precio(8000.0).precioBase(10000.0).build()));

        MvcResult result = mockMvc.perform(get("/api/presentaciones/con-descuento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(2))
                .andReturn();
        sout("conDescuento200", result);
    }

    @Test
    void filtrarPorConfiteria200() throws Exception {
        when(presentacionServicio.listarPorConfiteria(5)).thenReturn(List.of(
                ConfiteriaPresentacionResponse.builder().codigo(3).build()));

        MvcResult result = mockMvc.perform(get("/api/presentaciones").param("confiteria", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(3))
                .andReturn();
        sout("filtrarPorConfiteria200", result);
    }

    @Test
    void obtenerInexistente404() throws Exception {
        when(presentacionServicio.obtener(999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/presentaciones/999"))
                .andExpect(status().isNotFound())
                .andReturn();
        sout("obtener404", result);
    }

    @Test
    void registrarComoAdmin201() throws Exception {
        ConfiteriaPresentacionResponse mock = ConfiteriaPresentacionResponse.builder().codigo(1).precio(10000.0).build();
        when(presentacionServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"porcion":1.0,"unidadMedida":"UNIDAD","precio":10000.0,"precioBase":10000.0,"confiteriaCodigo":5}
                """;

        MvcResult result = mockMvc.perform(post("/api/presentaciones")
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
                {"porcion":1.0,"unidadMedida":"UNIDAD","precio":10000.0,"precioBase":10000.0,"confiteriaCodigo":5}
                """;

        MvcResult result = mockMvc.perform(post("/api/presentaciones")
                        .with(user(principalCliente()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("registrarCliente403", result);
    }
}
