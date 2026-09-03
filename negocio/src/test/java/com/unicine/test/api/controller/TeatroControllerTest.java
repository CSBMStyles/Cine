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

import com.unicine.api.controller.TeatroController;
import com.unicine.transfer.dto.response.CiudadResponse;
import com.unicine.transfer.dto.response.TeatroResponse;
import com.unicine.util.config.SecurityConfig;
import com.unicine.service.theater.TeatroServicio;

@WebMvcTest(controllers = TeatroController.class)
@Import(SecurityConfig.class)
class TeatroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeatroServicio teatroServicio;

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    @WithMockUser
    void registrarTeatro201() throws Exception {
        TeatroResponse mock = TeatroResponse.builder()
                .codigo(10).direccion("Calle 123").telefono("3001234567")
                .ciudad(CiudadResponse.builder().codigo(1).nombre("Armenia").build())
                .build();
        when(teatroServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"direccion":"Calle 123","telefono":"3001234567","ciudadCodigo":1,"administradorTeatroCedula":1119000000}
                """;

        MvcResult result = mockMvc.perform(post("/api/teatros").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.direccion").value("Calle 123"))
                .andReturn();

        sout("registrarTeatro201", result);
    }

    @Test
    void listarTeatrosPublico200() throws Exception {
        when(teatroServicio.listar()).thenReturn(List.of(
                TeatroResponse.builder().codigo(1).direccion("Calle 1").build()));

        MvcResult result = mockMvc.perform(get("/api/teatros"))
                .andExpect(status().isOk())
                .andReturn();

        sout("listarTeatros200", result);
    }

    @Test
    @WithMockUser
    void eliminarConConfirmacion204() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/teatros/1")
                        .with(csrf()).param("confirmacion", "true"))
                .andExpect(status().isNoContent())
                .andReturn();

        sout("eliminarConConfirmacion204", result);
    }

    @Test
    @WithMockUser
    void eliminarSinConfirmacionParam400() throws Exception {
        // sin ?confirmacion= → 400 HandlerMethodValidation → ApiError details
        MvcResult result = mockMvc.perform(delete("/api/teatros/1").with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();

        sout("eliminarSinConfirmacion400", result);
    }

    @Test
    void registrarSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/teatros").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"direccion\":\"Calle 123\",\"telefono\":\"3001234567\",\"ciudadCodigo\":1,\"administradorTeatroCedula\":1119000000}"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        sout("registrarSinAuth401", result);
    }
}
