package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.SalaController;
import com.unicine.enums.theater.TipoSala;
import com.unicine.service.theater.SalaServicio;
import com.unicine.transfer.dto.response.SalaResponse;
import com.unicine.util.config.SecurityConfig;

@WebMvcTest(controllers = SalaController.class)
@Import(SecurityConfig.class)
class SalaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalaServicio salaServicio;

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    @WithMockUser
    void registrarSala201() throws Exception {
        SalaResponse mock = SalaResponse.builder().codigo(5).nombre("Sala 3D").tipoSala(TipoSala.VIP).build();
        when(salaServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"nombre":"Sala 3D","tipoSala":"VIP","teatroCodigo":1,"distribucionSillaCodigo":1}
                """;

        MvcResult result = mockMvc.perform(post("/api/salas").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Sala 3D"))
                .andReturn();

        sout("registrarSala201", result);
    }

    @Test
    void listarSalas200() throws Exception {
        when(salaServicio.listar()).thenReturn(List.of(
                SalaResponse.builder().codigo(1).nombre("Sala 1").tipoSala(TipoSala.VIP).build()));

        MvcResult result = mockMvc.perform(get("/api/salas"))
                .andExpect(status().isOk())
                .andReturn();

        sout("listarSalas200", result);
    }

    @Test
    @WithMockUser
    void registrarSalaBodyInvalido400() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/salas").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray())
                .andReturn();

        sout("registrarSalaBodyInvalido400", result);
    }

    // SECTION: Filtros y paginacion 4.2.1

    @Test
    void filtrarPorTeatro200() throws Exception {
        when(salaServicio.listarPorTeatro(1)).thenReturn(List.of(
                SalaResponse.builder().codigo(1).nombre("Atlantis").tipoSala(TipoSala.XD).build()));

        MvcResult result = mockMvc.perform(get("/api/salas").param("teatro", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Atlantis"))
                .andReturn();

        sout("filtrarPorTeatro200", result);
    }

    @Test
    void filtrarPorNombreYTeatro200() throws Exception {
        when(salaServicio.obtenerNombresTeatro("Atlantis", 5)).thenReturn(List.of(
                SalaResponse.builder().codigo(1).nombre("Atlantis").tipoSala(TipoSala.XD).build()));

        MvcResult result = mockMvc.perform(get("/api/salas").param("nombre", "Atlantis").param("teatro", "5"))
                .andExpect(status().isOk())
                .andReturn();

        sout("filtrarPorNombreYTeatro200", result);
    }

    @Test
    void listarPaginado200() throws Exception {
        when(salaServicio.listarPaginado(any())).thenReturn(List.of(
                SalaResponse.builder().codigo(1).nombre("Sala 1").tipoSala(TipoSala.VIP).build()));

        MvcResult result = mockMvc.perform(get("/api/salas").param("page", "0").param("size", "5").param("sort", "nombre"))
                .andExpect(status().isOk())
                .andReturn();

        sout("listarPaginado200", result);
    }

    @Test
    void obtenerPorTeatro200() throws Exception {
        when(salaServicio.obtenerIdTeatro(1, 5)).thenReturn(java.util.Optional.of(
                SalaResponse.builder().codigo(1).nombre("Atlantis").tipoSala(TipoSala.XD).build()));

        MvcResult result = mockMvc.perform(get("/api/salas/teatro/5/sala/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Atlantis"))
                .andReturn();

        sout("obtenerPorTeatro200", result);
    }

    @Test
    void obtenerPorTeatro404() throws Exception {
        when(salaServicio.obtenerIdTeatro(99, 99)).thenReturn(java.util.Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/salas/teatro/99/sala/99"))
                .andExpect(status().isNotFound())
                .andReturn();

        sout("obtenerPorTeatro404", result);
    }

    @Test
    void registrarSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/salas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Sala X\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_INVALID_CREDENTIALS"))
                .andReturn();

        sout("registrarSinAuth401", result);
    }

    // !SECTION
}
