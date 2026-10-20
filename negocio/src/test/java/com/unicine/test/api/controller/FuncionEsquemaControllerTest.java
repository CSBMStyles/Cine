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

import com.unicine.api.controller.FuncionEsquemaController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.showing.FuncionEsquemaServicio;
import com.unicine.service.showing.FuncionServicio;
import com.unicine.transfer.dto.response.FuncionEsquemaResponse;
import com.unicine.transfer.dto.response.FuncionResponse;
import com.unicine.transfer.dto.response.SalaResponse;
import com.unicine.transfer.dto.response.TeatroResponse;
import com.unicine.util.config.SecurityConfig;
import com.unicine.util.validation.catalog.domain.ShowingErrorCatalog;

/**
 * Tests slice para FuncionEsquemaController.
 */
@WebMvcTest(controllers = FuncionEsquemaController.class)
@Import(SecurityConfig.class)
class FuncionEsquemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FuncionEsquemaServicio esquemaServicio;

    @MockitoBean
    private FuncionServicio funcionServicio;

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
        when(esquemaServicio.listar()).thenReturn(List.of(
                FuncionEsquemaResponse.builder().codigo(1).funcionCodigo(6).build()));

        MvcResult result = mockMvc.perform(get("/api/funcion-esquemas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("listarPublico200", result);
    }

    @Test
    void filtrarPorFuncion200() throws Exception {
        when(esquemaServicio.listar()).thenReturn(List.of(
                FuncionEsquemaResponse.builder().codigo(1).funcionCodigo(6).build(),
                FuncionEsquemaResponse.builder().codigo(2).funcionCodigo(7).build()));

        MvcResult result = mockMvc.perform(get("/api/funcion-esquemas").param("funcion", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("filtrarPorFuncion200", result);
    }

    @Test
    void obtenerInexistente404() throws Exception {
        when(esquemaServicio.obtener(999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/funcion-esquemas/999"))
                .andExpect(status().isNotFound())
                .andReturn();
        sout("obtener404", result);
    }

    @Test
    void registrarTeatroAjeno403() throws Exception {
        FuncionResponse funcion = FuncionResponse.builder().codigo(6)
                .sala(SalaResponse.builder().codigo(5).teatro(TeatroResponse.builder().codigo(1).build()).build())
                .build();
        when(funcionServicio.obtener(6)).thenReturn(Optional.of(funcion));

        UsuarioPrincipal ajeno = new UsuarioPrincipal(
                3001, "teatro@test.com", "hash", TipoUsuario.ADMINISTRADOR_TEATRO, List.of(99));
        String body = """
                {"ocupadas":0,"disponibles":96,"mantenimiento":0,"funcionCodigo":6}
                """;

        MvcResult result = mockMvc.perform(post("/api/funcion-esquemas")
                        .with(user(ajeno))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();

        sout("registrarEsquemaTeatroAjeno403", result);
    }

    @Test
    void registrarComoAdmin201() throws Exception {
        FuncionEsquemaResponse mock = FuncionEsquemaResponse.builder().codigo(1).funcionCodigo(6).build();
        when(esquemaServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"ocupadas":0,"disponibles":96,"mantenimiento":0,"funcionCodigo":6}
                """;

        MvcResult result = mockMvc.perform(post("/api/funcion-esquemas")
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
                {"ocupadas":0,"disponibles":96,"mantenimiento":0,"funcionCodigo":6}
                """;

        MvcResult result = mockMvc.perform(post("/api/funcion-esquemas")
                        .with(user(principalCliente()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("registrarCliente403", result);
    }

    @Test
    void registrarConFuncionInexistente404() throws Exception {
        when(funcionServicio.obtener(999)).thenThrow(new ResourceNotFoundException(
                ShowingErrorCatalog.DOMAIN_SHOWING_ENTITY_FUNCTION_NOT_FOUND));

        String body = """
                {"ocupadas":0,"disponibles":96,"mantenimiento":0,"funcionCodigo":999}
                """;

        MvcResult result = mockMvc.perform(post("/api/funcion-esquemas")
                        .with(user(principalAdmin()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound())
                .andReturn();
        sout("registrarFuncionInexistente404", result);
    }
}
