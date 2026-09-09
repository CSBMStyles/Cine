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

import com.unicine.api.controller.DistribucionSillaController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.theater.DistribucionSillaServicio;
import com.unicine.transfer.dto.response.DistribucionSillaResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para DistribucionSillaController — todo autenticado.
 */
@WebMvcTest(controllers = DistribucionSillaController.class)
@Import(SecurityConfig.class)
class DistribucionSillaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DistribucionSillaServicio distribucionServicio;

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
    void listarSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/distribuciones-silla"))
                .andExpect(status().isUnauthorized())
                .andReturn();
        sout("listar401", result);
    }

    @Test
    void listarConAuth200() throws Exception {
        when(distribucionServicio.listar()).thenReturn(List.of(
                DistribucionSillaResponse.builder().codigo(1).filas(8).columnas(12).build()));

        MvcResult result = mockMvc.perform(get("/api/distribuciones-silla")
                        .with(user(principalCliente())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("listar200", result);
    }

    @Test
    void obtenerInexistente404() throws Exception {
        when(distribucionServicio.obtener(999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/distribuciones-silla/999")
                        .with(user(principalCliente())))
                .andExpect(status().isNotFound())
                .andReturn();
        sout("obtener404", result);
    }

    @Test
    void registrarComoAdmin201() throws Exception {
        DistribucionSillaResponse mock = DistribucionSillaResponse.builder().codigo(1).build();
        when(distribucionServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"esquema":"[[\\"D\\"]]","totalSillas":96,"filas":8,"columnas":12}
                """;

        MvcResult result = mockMvc.perform(post("/api/distribuciones-silla")
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
                {"esquema":"[[\\"D\\"]]","totalSillas":96,"filas":8,"columnas":12}
                """;

        MvcResult result = mockMvc.perform(post("/api/distribuciones-silla")
                        .with(user(principalCliente()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("registrarCliente403", result);
    }
}
