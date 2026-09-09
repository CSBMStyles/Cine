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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.AdministradorController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.user.AdministradorServicio;
import com.unicine.transfer.dto.response.AdministradorResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para AdministradorController — ownership y rol ADMIN.
 */
@WebMvcTest(controllers = AdministradorController.class)
@Import(SecurityConfig.class)
class AdministradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdministradorServicio administradorServicio;

    private UsuarioPrincipal principalAdmin(Integer cedula) {
        return new UsuarioPrincipal(cedula, "admin@test.com", "hash", TipoUsuario.ADMINISTRADOR);
    }

    private UsuarioPrincipal principalCliente(Integer cedula) {
        return new UsuarioPrincipal(cedula, "pepe@test.com", "hash", TipoUsuario.CLIENTE);
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    // SECTION: Listar protegido

    @Test
    void listarSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/administradores"))
                .andExpect(status().isUnauthorized())
                .andReturn();
        sout("listarSinAuth401", result);
    }

    @Test
    void listarComoCliente403() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/administradores")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("listarComoCliente403", result);
    }

    @Test
    void listarComoAdmin200() throws Exception {
        when(administradorServicio.listar()).thenReturn(List.of(
                AdministradorResponse.builder().cedula(2001).correo("admin@test.com").build()));

        MvcResult result = mockMvc.perform(get("/api/administradores")
                        .with(user(principalAdmin(2001))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correo").value("admin@test.com"))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andReturn();
        sout("listarComoAdmin200", result);
    }

    // !SECTION
    // SECTION: Obtener por cedula

    @Test
    void obtenerOtroComoCliente403() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/administradores/2001")
                        .with(user(principalCliente(1009000011))))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("obtenerOtroComoCliente403", result);
    }

    @Test
    void obtenerPropioComoAdmin200() throws Exception {
        AdministradorResponse mock = AdministradorResponse.builder()
                .cedula(2001).correo("admin@test.com").build();
        when(administradorServicio.obtener(2001)).thenReturn(Optional.of(mock));

        MvcResult result = mockMvc.perform(get("/api/administradores/2001")
                        .with(user(principalAdmin(2001))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("admin@test.com"))
                .andReturn();
        sout("obtenerPropio200", result);
    }

    @Test
    void obtenerInexistente404() throws Exception {
        when(administradorServicio.obtener(9999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/administradores/9999")
                        .with(user(principalAdmin(2001))))
                .andExpect(status().isNotFound())
                .andReturn();
        sout("obtenerInexistente404", result);
    }

    // !SECTION
    // SECTION: Perfil propio

    @Test
    void actualizarMiPerfilIgnoraCedulaBody() throws Exception {
        AdministradorResponse mock = AdministradorResponse.builder()
                .cedula(2001).correo("nuevo@test.com").build();
        when(administradorServicio.actualizar(any())).thenReturn(mock);

        String body = """
                {"cedula":9999,"nombre":"Ana","apellido":"Admin","correo":"nuevo@test.com","password":"Aa1!aaaaa"}
                """;

        MvcResult result = mockMvc.perform(put("/api/administradores/me")
                        .with(user(principalAdmin(2001)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cedula").value(2001))
                .andReturn();
        sout("actualizarMeIgnoraCedula", result);
    }

    @Test
    void eliminarOtroComoCliente403() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/administradores/2001")
                        .param("confirmacion", "true")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("eliminarOtro403", result);
    }

    // !SECTION
}
