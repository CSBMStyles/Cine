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
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ValidationException;

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

    @MockitoBean
    private com.unicine.security.JwtServicio jwtServicio;

    @MockitoBean
    private com.unicine.service.auth.RefrescoServicio refrescoServicio;

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

    @Test
    void registroCorreoDuplicado409() throws Exception {
        when(clienteServicio.registrar(any()))
                .thenThrow(new ValidationException(
                        UserErrorCatalog.DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED));

        String body = """
                {"cedula":1009000011,"nombre":"Pepe","apellido":"Perez","correo":"pepe@test.com","password":"Aa1!aaaaa","estado":true,"fechaNacimiento":"2001-12-14","telefonos":["+573001234567"]}
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/registro").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED"))
                .andReturn();

        sout("registroCorreoDuplicado409", result);
    }

    @Test
    void registroCedulaDuplicada409() throws Exception {
        when(clienteServicio.registrar(any()))
                .thenThrow(new ValidationException(
                        UserErrorCatalog.DOMAIN_USER_DUPLICATE_ID_ALREADY_REGISTERED));

        String body = """
                {"cedula":1009000011,"nombre":"Pepe","apellido":"Perez","correo":"otro@test.com","password":"Aa1!aaaaa","estado":true,"fechaNacimiento":"2001-12-14","telefonos":["+573001234567"]}
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/registro").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_DUPLICATE_ID_ALREADY_REGISTERED"))
                .andReturn();

        sout("registroCedulaDuplicada409", result);
    }

    @Test
    void registroMenorEdad400() throws Exception {
        when(clienteServicio.registrar(any()))
                .thenThrow(new BusinessRuleException(
                        UserErrorCatalog.DOMAIN_USER_BUSINESS_RULE_CLIENT_UNDERAGE));

        String body = """
                {"cedula":1009000011,"nombre":"Pepe","apellido":"Perez","correo":"pepe@test.com","password":"Aa1!aaaaa","estado":true,"fechaNacimiento":"2015-01-01","telefonos":["+573001234567"]}
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/registro").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_BUSINESS_RULE_CLIENT_UNDERAGE"))
                .andReturn();

        sout("registroMenorEdad400", result);
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
        when(jwtServicio.emitirAcceso(any())).thenReturn("test-access-token");
        when(refrescoServicio.crearSesion(any())).thenReturn(
                com.unicine.transfer.dto.auth.ParTokensResponse.builder()
                        .accessToken("test-access-token").refreshToken("test-refresh-token").build());

        String body = """
                {"correo":"pepe@test.com","password":"Aa1!aaaaa"}
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("CLIENTE"))
                .andExpect(jsonPath("$.correo").value("pepe@test.com"))
                .andExpect(jsonPath("$.accessToken").value("test-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("test-refresh-token"))
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
    // SECTION: Refresh y logout

    @Test
    void refreshRota200() throws Exception {
        when(refrescoServicio.rotar("viejo")).thenReturn(
                com.unicine.transfer.dto.auth.ParTokensResponse.builder()
                        .accessToken("nuevo-access").refreshToken("nuevo-refresh").build());

        MvcResult result = mockMvc.perform(post("/api/auth/refresh").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"refreshToken\":\"viejo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("nuevo-access"))
                .andExpect(jsonPath("$.refreshToken").value("nuevo-refresh"))
                .andReturn();

        sout("refreshRota200", result);
    }

    @Test
    void logoutSiempre200() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/logout").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"refreshToken\":\"cualquiera\"}"))
                .andExpect(status().isOk())
                .andReturn();

        sout("logout200", result);
    }

    // !SECTION
}
