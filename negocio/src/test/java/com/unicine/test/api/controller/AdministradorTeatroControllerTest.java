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

import com.unicine.api.controller.AdministradorTeatroController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.user.AdministradorTeatroServicio;
import com.unicine.transfer.dto.response.AdministradorTeatroResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para AdministradorTeatroController — espejo de admin + alta ADMIN.
 */
@WebMvcTest(controllers = AdministradorTeatroController.class)
@Import(SecurityConfig.class)
class AdministradorTeatroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdministradorTeatroServicio administradorTeatroServicio;

    private UsuarioPrincipal principalAdmin() {
        return new UsuarioPrincipal(2001, "admin@test.com", "hash", TipoUsuario.ADMINISTRADOR);
    }

    private UsuarioPrincipal principalTeatro(Integer cedula) {
        return new UsuarioPrincipal(cedula, "teatro@test.com", "hash", TipoUsuario.ADMINISTRADOR_TEATRO);
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    void registrarComoAdmin201() throws Exception {
        AdministradorTeatroResponse mock = AdministradorTeatroResponse.builder()
                .cedula(3001).correo("teatro@test.com").build();
        when(administradorTeatroServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"cedula":3001,"nombre":"Ana","apellido":"Teatro","correo":"teatro@test.com","password":"Aa1!aaaaa"}
                """;

        MvcResult result = mockMvc.perform(post("/api/administradores-teatro")
                        .with(user(principalAdmin()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cedula").value(3001))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();
        sout("registrarAdmin201", result);
    }

    @Test
    void registrarComoTeatro403() throws Exception {
        String body = """
                {"cedula":3002,"nombre":"Bob","apellido":"Teatro","correo":"bob@test.com","password":"Aa1!aaaaa"}
                """;

        MvcResult result = mockMvc.perform(post("/api/administradores-teatro")
                        .with(user(principalTeatro(3001)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("registrarTeatro403", result);
    }

    @Test
    void listarComoAdmin200() throws Exception {
        when(administradorTeatroServicio.listar()).thenReturn(List.of(
                AdministradorTeatroResponse.builder().cedula(3001).correo("teatro@test.com").build()));

        MvcResult result = mockMvc.perform(get("/api/administradores-teatro")
                        .with(user(principalAdmin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cedula").value(3001))
                .andReturn();
        sout("listarAdmin200", result);
    }

    @Test
    void listarComoTeatro403() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/administradores-teatro")
                        .with(user(principalTeatro(3001))))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("listarTeatro403", result);
    }

    @Test
    void obtenerPropio200() throws Exception {
        AdministradorTeatroResponse mock = AdministradorTeatroResponse.builder()
                .cedula(3001).correo("teatro@test.com").build();
        when(administradorTeatroServicio.obtener(3001)).thenReturn(Optional.of(mock));

        MvcResult result = mockMvc.perform(get("/api/administradores-teatro/3001")
                        .with(user(principalTeatro(3001))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cedula").value(3001))
                .andReturn();
        sout("obtenerPropio200", result);
    }

    @Test
    void obtenerOtroComoTeatro403() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/administradores-teatro/3002")
                        .with(user(principalTeatro(3001))))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("obtenerOtro403", result);
    }
}
