package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import com.unicine.api.controller.HorarioController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.exception.BusinessRuleException;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.showing.HorarioServicio;
import com.unicine.transfer.dto.response.HorarioResponse;
import com.unicine.util.config.SecurityConfig;
import com.unicine.util.validation.catalog.domain.ShowingErrorCatalog;

/**
 * Tests slice para HorarioController — solape 400 con codigo de dominio.
 */
@WebMvcTest(controllers = HorarioController.class)
@Import(SecurityConfig.class)
class HorarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HorarioServicio horarioServicio;

    private UsuarioPrincipal principalTeatro() {
        return new UsuarioPrincipal(3001, "teatro@test.com", "hash", TipoUsuario.ADMINISTRADOR_TEATRO);
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
        when(horarioServicio.listar()).thenReturn(List.of(
                HorarioResponse.builder().codigo(1).build()));

        MvcResult result = mockMvc.perform(get("/api/horarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("listarPublico200", result);
    }

    @Test
    void obtenerInexistente404() throws Exception {
        when(horarioServicio.obtener(999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/horarios/999"))
                .andExpect(status().isNotFound())
                .andReturn();
        sout("obtener404", result);
    }

    @Test
    void registrarComoTeatro201() throws Exception {
        HorarioResponse mock = HorarioResponse.builder().codigo(1).build();
        when(horarioServicio.registrar(any(), eq(1))).thenReturn(mock);

        String body = """
                {"fechaInicio":"2030-01-01T10:00:00","fechaFin":"2030-01-01T12:00:00"}
                """;

        MvcResult result = mockMvc.perform(post("/api/horarios")
                        .param("salaCodigo", "1")
                        .with(user(principalTeatro()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(1))
                .andReturn();
        sout("registrarTeatro201", result);
    }

    @Test
    void registrarSolapado400() throws Exception {
        when(horarioServicio.registrar(any(), eq(1)))
                .thenThrow(new BusinessRuleException(
                        ShowingErrorCatalog.DOMAIN_SHOWING_BUSINESS_RULE_SCHEDULE_OVERLAP));

        String body = """
                {"fechaInicio":"2030-01-01T10:00:00","fechaFin":"2030-01-01T12:00:00"}
                """;

        MvcResult result = mockMvc.perform(post("/api/horarios")
                        .param("salaCodigo", "1")
                        .with(user(principalTeatro()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DOMAIN_SHOWING_BUSINESS_RULE_SCHEDULE_OVERLAP"))
                .andReturn();
        sout("solape400", result);
    }

    @Test
    void registrarComoCliente403() throws Exception {
        String body = """
                {"fechaInicio":"2030-01-01T10:00:00","fechaFin":"2030-01-01T12:00:00"}
                """;

        MvcResult result = mockMvc.perform(post("/api/horarios")
                        .param("salaCodigo", "1")
                        .with(user(principalCliente()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("registrarCliente403", result);
    }
}
