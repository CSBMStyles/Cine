package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.ConfiteriaController;
import com.unicine.enums.confiteria.CategoriaConfiteria;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.confiteria.ConfiteriaServicio;
import com.unicine.transfer.dto.response.ConfiteriaResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para ConfiteriaController — catalogo publico + escritura protegida.
 */
@WebMvcTest(controllers = ConfiteriaController.class)
@Import(SecurityConfig.class)
class ConfiteriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConfiteriaServicio confiteriaServicio;

    private UsuarioPrincipal principalAdmin() {
        return new UsuarioPrincipal(2001, "admin@test.com", "hash", TipoUsuario.ADMINISTRADOR);
    }

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
        when(confiteriaServicio.listar()).thenReturn(List.of(
                ConfiteriaResponse.builder().codigo(1).nombre("Crispeta").categoria(CategoriaConfiteria.SNACK).build()));

        MvcResult result = mockMvc.perform(get("/api/confiterias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("listarPublico200", result);
    }

    @Test
    void filtrarPorCategoria200() throws Exception {
        when(confiteriaServicio.listarPorCategoria(CategoriaConfiteria.SNACK)).thenReturn(List.of(
                ConfiteriaResponse.builder().codigo(1).categoria(CategoriaConfiteria.SNACK).build()));

        MvcResult result = mockMvc.perform(get("/api/confiterias").param("categoria", "SNACK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("filtrarPorCategoria200", result);
    }

    @Test
    void obtenerInexistente404() throws Exception {
        when(confiteriaServicio.obtener(999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/confiterias/999"))
                .andExpect(status().isNotFound())
                .andReturn();
        sout("obtener404", result);
    }

    @Test
    void registrarComoAdminTeatro201() throws Exception {
        ConfiteriaResponse mock = ConfiteriaResponse.builder().codigo(1).nombre("Crispeta").build();
        when(confiteriaServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"nombre":"Crispeta","descripcion":"Crispeta mediana","categoria":"SNACK"}
                """;

        MvcResult result = mockMvc.perform(post("/api/confiterias")
                        .with(user(principalTeatro()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(1))
                .andReturn();
        sout("registrarTeatro201", result);
    }

    @Test
    void registrarComoCliente403() throws Exception {
        String body = """
                {"nombre":"Crispeta","descripcion":"Crispeta mediana","categoria":"SNACK"}
                """;

        MvcResult result = mockMvc.perform(post("/api/confiterias")
                        .with(user(principalCliente()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("registrarCliente403", result);
    }

    @Test
    void registrarBodyInvalido400() throws Exception {
        String body = """
                {"nombre":"","categoria":"SNACK"}
                """;

        MvcResult result = mockMvc.perform(post("/api/confiterias")
                        .with(user(principalAdmin()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andReturn();
        sout("registrarBody400", result);
    }

    @Test
    void eliminarComoCliente403() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/confiterias/1")
                        .param("confirmacion", "true")
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("eliminarCliente403", result);
    }
}
