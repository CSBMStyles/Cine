package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.ColeccionController;
import com.unicine.service.movie.ColeccionServicio;
import com.unicine.transfer.dto.response.ColeccionResponse;
import com.unicine.util.config.SecurityConfig;

@WebMvcTest(controllers = ColeccionController.class)
@Import(SecurityConfig.class)
class ColeccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ColeccionServicio coleccionServicio;

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    @WithMockUser
    void registrarColeccion201() throws Exception {
        ColeccionResponse mock = ColeccionResponse.builder().puntuacion(4.5).notificacionActiva(true).build();
        when(coleccionServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"clienteCedula":1009000011,"peliculaCodigo":1,"puntuacion":4.5,"notificacionActiva":true}
                """;

        MvcResult result = mockMvc.perform(post("/api/colecciones").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn();

        sout("registrarColeccion201", result);
    }

    @Test
    @WithMockUser
    void listarPorCliente200() throws Exception {
        when(coleccionServicio.listarPorCliente(1009000011)).thenReturn(List.of(
                ColeccionResponse.builder().puntuacion(5.0).build()));

        MvcResult result = mockMvc.perform(get("/api/colecciones").param("cliente", "1009000011"))
                .andExpect(status().isOk())
                .andReturn();

        sout("listarPorCliente200 ?cliente=1009000011", result);
    }

    @Test
    @WithMockUser
    void obtenerPorClaveCompuesta200() throws Exception {
        when(coleccionServicio.obtener(1009000011, 1)).thenReturn(Optional.of(
                ColeccionResponse.builder().puntuacion(4.0).build()));

        MvcResult result = mockMvc.perform(get("/api/colecciones/1009000011/1"))
                .andExpect(status().isOk())
                .andReturn();

        sout("obtenerPorClaveCompuesta200 /1009000011/1", result);
    }

    @Test
    @WithMockUser
    void eliminarConConfirmacion204() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/colecciones/1009000011/1")
                        .with(csrf()).param("confirmacion", "true"))
                .andExpect(status().isNoContent())
                .andReturn();

        sout("eliminarConConfirmacion204", result);
    }

    @Test
    void registrarSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/colecciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteCedula\":1,\"peliculaCodigo\":1}"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        sout("registrarSinAuth401", result);
    }

    @Test
    void obtenerInexistente404() throws Exception {
        when(coleccionServicio.obtener(99, 99)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/colecciones/99/99").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isNotFound())
                .andReturn();

        sout("obtenerInexistente404", result);
    }

    @Test
    @WithMockUser
    void registrarBodyInvalido400() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/colecciones").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray())
                .andReturn();

        sout("registrarBodyInvalido400", result);
    }
}
