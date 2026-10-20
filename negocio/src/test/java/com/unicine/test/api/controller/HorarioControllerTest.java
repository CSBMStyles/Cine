package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.HorarioController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.exception.BusinessRuleException;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.showing.HorarioServicio;
import com.unicine.service.theater.SalaServicio;
import com.unicine.transfer.dto.response.HorarioResponse;
import com.unicine.transfer.dto.response.SalaResponse;
import com.unicine.transfer.dto.response.TeatroResponse;
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

    @MockitoBean
    private SalaServicio salaServicio;

    private UsuarioPrincipal principalTeatro() {
        return new UsuarioPrincipal(3001, "teatro@test.com", "hash", TipoUsuario.ADMINISTRADOR_TEATRO);
    }

    private UsuarioPrincipal principalCliente() {
        return new UsuarioPrincipal(1009000011, "pepe@test.com", "hash", TipoUsuario.CLIENTE);
    }

    private UsuarioPrincipal principalAdmin() {
        return new UsuarioPrincipal(2001, "admin@test.com", "hash", TipoUsuario.ADMINISTRADOR);
    }

    private String bodyHorario() {
        return """
                {"fechaInicio":"2030-01-01T10:00:00","fechaFin":"2030-01-01T12:00:00"}
                """;
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
    void registrarTeatroAjeno403() throws Exception {
        when(salaServicio.obtener(1)).thenReturn(Optional.of(
                SalaResponse.builder().codigo(1).teatro(TeatroResponse.builder().codigo(1).build()).build()));

        UsuarioPrincipal ajeno = new UsuarioPrincipal(
                3001, "teatro@test.com", "hash", TipoUsuario.ADMINISTRADOR_TEATRO, List.of(99));
        String body = """
                {"fechaInicio":"2030-01-01T10:00:00","fechaFin":"2030-01-01T12:00:00"}
                """;

        MvcResult result = mockMvc.perform(post("/api/horarios")
                        .param("salaCodigo", "1")
                        .with(user(ajeno))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();

        sout("registrarHorarioTeatroAjeno403", result);
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
        MvcResult result = mockMvc.perform(post("/api/horarios")
                        .param("salaCodigo", "1")
                        .with(user(principalCliente()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(bodyHorario()))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("registrarCliente403", result);
    }

    @Test
    void actualizarTeatroAjeno403() throws Exception {
        when(horarioServicio.obtener(5)).thenReturn(Optional.of(
                HorarioResponse.builder().codigo(5).salaCodigo(1).teatroCodigo(1).build()));

        UsuarioPrincipal ajeno = new UsuarioPrincipal(
                3001, "teatro@test.com", "hash", TipoUsuario.ADMINISTRADOR_TEATRO, List.of(99));

        MvcResult result = mockMvc.perform(put("/api/horarios/5")
                        .with(user(ajeno))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(bodyHorario()))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("actualizarHorarioTeatroAjeno403", result);
    }

    @Test
    void actualizarPropio200() throws Exception {
        when(horarioServicio.obtener(5)).thenReturn(Optional.of(
                HorarioResponse.builder().codigo(5).salaCodigo(1).teatroCodigo(1).build()));
        when(horarioServicio.actualizar(any())).thenReturn(
                HorarioResponse.builder().codigo(5).salaCodigo(1).teatroCodigo(1).build());

        UsuarioPrincipal propio = new UsuarioPrincipal(
                3001, "teatro@test.com", "hash", TipoUsuario.ADMINISTRADOR_TEATRO, List.of(1));

        MvcResult result = mockMvc.perform(put("/api/horarios/5")
                        .with(user(propio))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(bodyHorario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(5))
                .andReturn();
        sout("actualizarHorarioPropio200", result);
    }

    @Test
    void actualizarAdmin200() throws Exception {
        when(horarioServicio.obtener(5)).thenReturn(Optional.of(
                HorarioResponse.builder().codigo(5).salaCodigo(1).teatroCodigo(1).build()));
        when(horarioServicio.actualizar(any())).thenReturn(
                HorarioResponse.builder().codigo(5).salaCodigo(1).teatroCodigo(1).build());

        MvcResult result = mockMvc.perform(put("/api/horarios/5")
                        .with(user(principalAdmin()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(bodyHorario()))
                .andExpect(status().isOk())
                .andReturn();
        sout("actualizarHorarioAdmin200", result);
    }
}
