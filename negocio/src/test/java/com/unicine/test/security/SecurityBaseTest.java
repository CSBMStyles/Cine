package com.unicine.test.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.unicine.util.config.SecurityConfig;

/**
 * Pruebas de contrato para la configuracion base de seguridad (sin JWT).
 *
 * Verifica rutas publicas/privadas, 401/403 con ApiError, stateless y CSRF.
 */
@WebMvcTest(controllers = SecurityTestController.class)
@Import(SecurityConfig.class)
class SecurityBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // SECTION: Rutas publicas

    @Test
    void rutaPublicaSinAutenticacionResponde200() throws Exception {
        mockMvc.perform(get("/security/public"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Set-Cookie"));
    }

    @Test
    void rutaPublicaPostSinCsrfResponde200() throws Exception {
        mockMvc.perform(post("/security/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Set-Cookie"));
    }

    // !SECTION
    // SECTION: Rutas privadas - 401

    @Test
    void rutaPrivadaSinAutenticacionResponde401ConApiError() throws Exception {
        mockMvc.perform(get("/security/private"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("Set-Cookie"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.path").value("/security/private"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void rutaPrivadaConAutenticacionResponde200() throws Exception {
        mockMvc.perform(get("/security/private")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("user")))
                .andExpect(status().isOk());
    }

    // !SECTION
    // SECTION: Autorizacion - 403

    @Test
    void rutaAdminSinAutenticacionResponde401() throws Exception {
        mockMvc.perform(get("/security/admin"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void rutaAdminConUsuarioSinRolResponde403ConApiError() throws Exception {
        mockMvc.perform(get("/security/admin"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Set-Cookie"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_ACTION_NOT_PERMITTED"))
                .andExpect(jsonPath("$.path").value("/security/admin"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void rutaAdminConRolAdminResponde200() throws Exception {
        mockMvc.perform(get("/security/admin"))
                .andExpect(status().isOk());
    }

    // !SECTION
    // SECTION: Stateless y PasswordEncoder

    @Test
    void noSeCreaSesionHttpEnRutaPublica() throws Exception {
        mockMvc.perform(get("/security/public"))
                .andExpect(header().doesNotExist("Set-Cookie"))
                .andExpect(status().isOk());
    }

    @Test
    void noSeCreaSesionHttpEn401() throws Exception {
        mockMvc.perform(get("/security/private"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("Set-Cookie"));
    }

    @Test
    void passwordEncoderCodificaYVerifica() {
        String raw = "Secreto123*";
        String encoded = passwordEncoder.encode(raw);

        assertThat(encoded).isNotEqualTo(raw);
        assertThat(passwordEncoder.matches(raw, encoded)).isTrue();
        assertThat(passwordEncoder.matches("otra", encoded)).isFalse();
    }

    @Test
    void respuestas401NoExponenStackTrace() throws Exception {
        mockMvc.perform(get("/security/private"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("stack"))))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Exception"))));
    }

    // !SECTION
}
