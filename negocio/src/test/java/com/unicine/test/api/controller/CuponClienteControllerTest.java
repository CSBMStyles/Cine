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

import com.unicine.api.controller.CuponClienteController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.purchase.CuponClienteServicio;
import com.unicine.transfer.dto.response.ClienteResponse;
import com.unicine.transfer.dto.response.CuponClienteResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para CuponClienteController — ownership propio o ADMIN.
 */
@WebMvcTest(controllers = CuponClienteController.class)
@Import(SecurityConfig.class)
class CuponClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CuponClienteServicio cuponClienteServicio;

    private UsuarioPrincipal principalAdmin() {
        return new UsuarioPrincipal(2001, "admin@test.com", "hash", TipoUsuario.ADMINISTRADOR);
    }

    private UsuarioPrincipal principalCliente(Integer cedula) {
        return new UsuarioPrincipal(cedula, "pepe@test.com", "hash", TipoUsuario.CLIENTE);
    }

    private CuponClienteResponse asignacion(Integer codigo, Integer cedula) {
        return CuponClienteResponse.builder()
                .codigo(codigo)
                .estado(true)
                .cliente(ClienteResponse.builder().cedula(cedula).build())
                .build();
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    void listarPropias200() throws Exception {
        when(cuponClienteServicio.listarPorCliente(1009000011))
                .thenReturn(List.of(asignacion(1, 1009000011)));

        MvcResult result = mockMvc.perform(get("/api/cupones-clientes")
                        .param("cliente", "1009000011")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("listarPropias200", result);
    }

    @Test
    void listarAjenas403() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cupones-clientes")
                        .param("cliente", "999")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("listarAjenas403", result);
    }

    @Test
    void obtenerAjeno403() throws Exception {
        when(cuponClienteServicio.obtener(1)).thenReturn(Optional.of(asignacion(1, 999)));

        MvcResult result = mockMvc.perform(get("/api/cupones-clientes/1")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("obtenerAjeno403", result);
    }

    @Test
    void contarRedimidosPropio200() throws Exception {
        when(cuponClienteServicio.contarRedimidosPorCliente(1009000011)).thenReturn(3L);

        MvcResult result = mockMvc.perform(get("/api/cupones-clientes/redimidos/count")
                        .param("cliente", "1009000011")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redimidos").value(3))
                .andReturn();
        sout("contarRedimidos200", result);
    }

    @Test
    void asignarComoAdmin201() throws Exception {
        when(cuponClienteServicio.registrar(any())).thenReturn(asignacion(1, 1009000011));

        String body = """
                {"estado":true,"cuponCodigo":1,"clienteCedula":1009000011}
                """;

        MvcResult result = mockMvc.perform(post("/api/cupones-clientes")
                        .with(user(principalAdmin()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(1))
                .andReturn();
        sout("asignarAdmin201", result);
    }

    @Test
    void asignarComoCliente403() throws Exception {
        String body = """
                {"estado":true,"cuponCodigo":1,"clienteCedula":1009000011}
                """;

        MvcResult result = mockMvc.perform(post("/api/cupones-clientes")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("asignarCliente403", result);
    }
}
