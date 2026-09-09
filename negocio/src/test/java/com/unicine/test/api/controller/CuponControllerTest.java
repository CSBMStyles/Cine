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

import com.unicine.api.controller.CuponController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.purchase.CuponServicio;
import com.unicine.transfer.dto.response.CuponResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para CuponController — lectura autenticada, escritura ADMIN.
 */
@WebMvcTest(controllers = CuponController.class)
@Import(SecurityConfig.class)
class CuponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CuponServicio cuponServicio;

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
    void activosConAuth200() throws Exception {
        when(cuponServicio.listarActivos()).thenReturn(List.of(
                CuponResponse.builder().codigo(1).criterio("Primer registro").build()));

        MvcResult result = mockMvc.perform(get("/api/cupones/activos")
                        .with(user(principalCliente())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("activos200", result);
    }

    @Test
    void activosSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cupones/activos"))
                .andExpect(status().isUnauthorized())
                .andReturn();
        sout("activos401", result);
    }

    @Test
    void buscarPorCriterio200() throws Exception {
        when(cuponServicio.buscarPorCriterio("registro")).thenReturn(List.of(
                CuponResponse.builder().codigo(1).build()));

        MvcResult result = mockMvc.perform(get("/api/cupones")
                        .param("criterio", "registro")
                        .with(user(principalCliente())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("buscarPorCriterio200", result);
    }

    @Test
    void obtenerInexistente404() throws Exception {
        when(cuponServicio.obtener(999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/cupones/999")
                        .with(user(principalCliente())))
                .andExpect(status().isNotFound())
                .andReturn();
        sout("obtener404", result);
    }

    @Test
    void registrarComoAdmin201() throws Exception {
        CuponResponse mock = CuponResponse.builder().codigo(1).criterio("Primer registro").build();
        when(cuponServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"descripcion":"Cupon 15% primer registro","descuento":15.0,"criterio":"Primer registro","fechaVencimiento":"2030-12-31T23:59:59"}
                """;

        MvcResult result = mockMvc.perform(post("/api/cupones")
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
                {"descripcion":"Cupon 15% primer registro","descuento":15.0,"criterio":"Primer registro","fechaVencimiento":"2030-12-31T23:59:59"}
                """;

        MvcResult result = mockMvc.perform(post("/api/cupones")
                        .with(user(principalCliente()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("registrarCliente403", result);
    }
}
