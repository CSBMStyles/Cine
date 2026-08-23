package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.AuthController;
import com.unicine.entity.user.Cliente;
import com.unicine.service.user.AdministradorServicio;
import com.unicine.service.user.AuthenticationService;
import com.unicine.service.user.ClienteServicio;
import com.unicine.transfer.dto.response.ClienteResponse;
import com.unicine.util.config.SecurityConfig;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;
import com.unicine.exception.AuthenticationException;

/**
 * Tests slice para AuthController — registro y login.
 * Sout visible gracias a testLogging.showStandardStreams=true.
 */
@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteServicio clienteServicio;

    @MockitoBean
    private AdministradorServicio administradorServicio;

    @MockitoBean
    private AuthenticationService authenticationService;

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    // SECTION: Registro

    @Test
    void registroClienteValido201() throws Exception {
        ClienteResponse mock = ClienteResponse.builder()
                .cedula(1009000011).nombre("Pepe").apellido("Perez").correo("pepe@test.com").estado(true).build();
        when(clienteServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"cedula":1009000011,"nombre":"Pepe","apellido":"Perez","correo":"pepe@test.com","password":"Aa1!aaaaa","estado":true,"fechaNacimiento":"2001-12-14","telefonos":["+573001234567"]}
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/registro").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correo").value("pepe@test.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        sout("registroClienteValido201", result);
    }

    @Test
    void registroPasswordSinMayuscula400() throws Exception {
        String body = """
                {"cedula":1009000011,"nombre":"Pepe","apellido":"Perez","correo":"pepe@test.com","password":"aa1!aaaaa","estado":true,"fechaNacimiento":"2001-12-14"}
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/registro").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray())
                .andReturn();

        sout("registroPasswordSinMayuscula400", result);
    }

    @Test
    void registroResponseNoExponePassword() throws Exception {
        ClienteResponse mock = ClienteResponse.builder()
                .cedula(1).correo("a@b.com").nombre("A").build();
        when(clienteServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"cedula":1,"nombre":"A","apellido":"B","correo":"a@b.com","password":"Aa1!aaaaa","estado":true,"fechaNacimiento":"2000-01-01"}
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/registro").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.correo").value("a@b.com"))
                .andReturn();

        sout("registroNoExponePassword", result);
    }

    // !SECTION
    // SECTION: Login

    @Test
    void loginValido200ConTipo() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setCedula(1009000011);
        cliente.setNombre("Pepe");
        cliente.setCorreo("pepe@test.com");
        cliente.setPassword("hashed");
        when(authenticationService.login("pepe@test.com", "Aa1!aaaaa")).thenReturn(cliente);

        String body = """
                {"correo":"pepe@test.com","password":"Aa1!aaaaa"}
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("CLIENTE"))
                .andExpect(jsonPath("$.correo").value("pepe@test.com"))
                .andReturn();

        sout("loginValido200", result);
    }

    @Test
    void loginCredencialesInvalidas401() throws Exception {
        when(authenticationService.login(any(), any()))
                .thenThrow(new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_INVALID_CREDENTIALS));

        String body = """
                {"correo":"bad@test.com","password":"Aa1!aaaaa"}
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_INVALID_CREDENTIALS"))
                .andReturn();

        sout("login401", result);
    }

    @Test
    void loginBodyInvalido400() throws Exception {
        String body = """
                {"correo":"","password":""}
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        sout("loginBodyInvalido400", result);
    }

    // !SECTION
}
