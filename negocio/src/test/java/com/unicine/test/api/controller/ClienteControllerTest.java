package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.ClienteController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.user.ClienteServicio;
import com.unicine.transfer.dto.response.ClienteResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para ClienteController — perfil /me y ownership.
 */
@WebMvcTest(controllers = ClienteController.class)
@Import(SecurityConfig.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteServicio clienteServicio;

    private UsuarioPrincipal principalCliente(Integer cedula) {
        // password dummy, authorities ROLE_CLIENTE
        return new UsuarioPrincipal(cedula, "pepe@test.com", "hashed", TipoUsuario.CLIENTE);
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    void getMeSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/clientes/me"))
                .andExpect(status().isUnauthorized())
                .andReturn();
        sout("getMeSinAuth401", result);
    }

    @Test
    void getMeConAuth200() throws Exception {
        ClienteResponse mock = ClienteResponse.builder().cedula(1009000011).nombre("Pepe").correo("pepe@test.com").estado(true).build();
        when(clienteServicio.obtener(1009000011)).thenReturn(Optional.of(mock));

        MvcResult result = mockMvc.perform(get("/api/clientes/me")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cedula").value(1009000011))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        sout("getMeConAuth200", result);
    }

    @Test
    void getOtroPerfilSinSerAdmin403() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/clientes/999")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isForbidden())
                .andReturn();

        sout("getOtroPerfil403", result);
    }

    @Test
    void listarRequiereAuth401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isUnauthorized())
                .andReturn();
        sout("listarSinAuth401", result);
    }

    @Test
    void eliminarOwnConConfirmacion204() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/clientes/1009000011")
                        .param("confirmacion", "true")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn();
        sout("eliminarOwn204", result);
    }
}
