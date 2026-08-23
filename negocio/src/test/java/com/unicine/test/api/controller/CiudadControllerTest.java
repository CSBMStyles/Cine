package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicine.api.controller.CiudadController;
import com.unicine.service.theater.CiudadServicio;
import com.unicine.transfer.dto.response.CiudadResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests de slice para CiudadController.
 * Cada test imprime en consola (sout) el JSON recibido para que veas el ApiError / Response.
 */
@WebMvcTest(controllers = CiudadController.class)
@Import(SecurityConfig.class)
class CiudadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CiudadServicio ciudadServicio;

    @Autowired
    private ObjectMapper objectMapper;

    // SECTION: Utilidad sout

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    // !SECTION
    // SECTION: Escritura

    @Test
    @WithMockUser
    void registrarCiudadValida201VisibleEnConsola() throws Exception {
        CiudadResponse mock = CiudadResponse.builder().codigo(6).nombre("Garzon").build();
        when(ciudadServicio.registrar(any())).thenReturn(mock);

        MvcResult result = mockMvc.perform(post("/api/ciudades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Garzon\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Garzon"))
                .andReturn();

        sout("registrarCiudadValida201", result);
    }

    @Test
    @WithMockUser
    void registrarCiudadNombreInvalido400ConDetails() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/ciudades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray())
                .andReturn();

        sout("registrarNombreInvalido400", result);
    }

    @Test
    void registrarSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/ciudades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Garzon\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_INVALID_CREDENTIALS"))
                .andReturn();

        sout("registrarSinAuth401", result);
    }

    // !SECTION
    // SECTION: Lectura

    @Test
    void listarCiudadesPublico200() throws Exception {
        when(ciudadServicio.listar()).thenReturn(List.of(
                CiudadResponse.builder().codigo(1).nombre("Armenia").build(),
                CiudadResponse.builder().codigo(2).nombre("Pereira").build()));

        MvcResult result = mockMvc.perform(get("/api/ciudades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Armenia"))
                .andReturn();

        sout("listarCiudadesPublico200", result);
    }

    @Test
    void listarPaginadoConPageable200() throws Exception {
        when(ciudadServicio.listarPaginado(any())).thenReturn(List.of(
                CiudadResponse.builder().codigo(1).nombre("Armenia").build()));

        MvcResult result = mockMvc.perform(get("/api/ciudades")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "nombre")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Armenia"))
                .andReturn();

        sout("listarPaginado200 ?page=0&size=5&sort=nombre", result);
    }

    @Test
    void obtenerPorIdExistente200() throws Exception {
        when(ciudadServicio.obtener(1)).thenReturn(Optional.of(
                CiudadResponse.builder().codigo(1).nombre("Armenia").build()));

        MvcResult result = mockMvc.perform(get("/api/ciudades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(1))
                .andReturn();

        sout("obtenerPorId200", result);
    }

    @Test
    void obtenerInexistente404() throws Exception {
        when(ciudadServicio.obtener(999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/ciudades/999"))
                .andExpect(status().isNotFound())
                .andReturn();

        sout("obtenerInexistente404", result);
    }

    // !SECTION
}
