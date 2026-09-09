package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.ImagenController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.image.ImagenServicio;
import com.unicine.transfer.dto.response.ImagenResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para ImagenController — multipart con gateway simulado.
 */
@WebMvcTest(controllers = ImagenController.class)
@Import(SecurityConfig.class)
class ImagenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImagenServicio imagenServicio;

    private UsuarioPrincipal principalCliente() {
        return new UsuarioPrincipal(1009000011, "pepe@test.com", "hash", TipoUsuario.CLIENTE);
    }

    private MockMultipartFile archivo() {
        return new MockMultipartFile("file", "foto.jpg", "image/jpeg", "fake-bytes".getBytes(StandardCharsets.UTF_8));
    }

    private MockMultipartFile datos() {
        String json = """
                {"codigo":"tmp-1","nombre":"foto","tipoPropietario":"PELICULA","codigoPropietario":1}
                """;
        return new MockMultipartFile("datos", "", "application/json", json.getBytes(StandardCharsets.UTF_8));
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    void subir201() throws Exception {
        ImagenResponse mock = ImagenResponse.builder().codigo("abc123").url("https://img.test/abc123").build();
        when(imagenServicio.registrar(any(), any())).thenReturn(mock);

        MvcResult result = mockMvc.perform(multipart("/api/imagenes")
                        .file(archivo())
                        .file(datos())
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("abc123"))
                .andReturn();
        sout("subir201", result);
    }

    @Test
    void subirSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/imagenes")
                        .file(archivo())
                        .file(datos())
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
        sout("subir401", result);
    }

    @Test
    void subirSinFile400() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/imagenes")
                        .file(datos())
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        sout("subirSinFile400", result);
    }

    @Test
    void obtenerInexistente404() throws Exception {
        when(imagenServicio.obtener("nope")).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/imagenes/nope")
                        .with(user(principalCliente())))
                .andExpect(status().isNotFound())
                .andReturn();
        sout("obtener404", result);
    }

    @Test
    void listarPorPropietario200() throws Exception {
        when(imagenServicio.listar(any(), any())).thenReturn(List.of("abc123"));

        MvcResult result = mockMvc.perform(get("/api/imagenes")
                        .param("tipoPropietario", "PELICULA")
                        .param("codigoPropietario", "1")
                        .with(user(principalCliente())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("abc123"))
                .andReturn();
        sout("listarPorPropietario200", result);
    }

    @Test
    void sdkNoExpuestoEnResponse() throws Exception {
        ImagenResponse mock = ImagenResponse.builder()
                .codigo("abc123")
                .url("https://img.test/abc123")
                .build();
        when(imagenServicio.obtener("abc123")).thenReturn(Optional.of(mock));

        MvcResult result = mockMvc.perform(get("/api/imagenes/abc123")
                        .with(user(principalCliente())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://img.test/abc123"))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assert !body.contains("ImageKit") && !body.contains("sdk");
        sout("sinSdkEnResponse", result);
    }
}
