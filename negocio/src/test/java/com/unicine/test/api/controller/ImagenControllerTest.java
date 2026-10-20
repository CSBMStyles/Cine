package com.unicine.test.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.ImagenController;
import com.unicine.enums.image.TipoPropietarioImagen;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.image.ImagenServicio;
import com.unicine.transfer.dto.request.ImagenRequest;
import com.unicine.transfer.dto.response.ImagenResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para ImagenController — multipart con gateway simulado.
 * Cubre lectura publica y ownership de escritura (5.2 B5).
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

    private UsuarioPrincipal principalAdmin() {
        return new UsuarioPrincipal(2001, "admin@test.com", "hash", TipoUsuario.ADMINISTRADOR);
    }

    private UsuarioPrincipal principalTeatro(Integer cedula) {
        return new UsuarioPrincipal(cedula, "teatro@test.com", "hash",
                TipoUsuario.ADMINISTRADOR_TEATRO, List.of(1));
    }

    private MockMultipartFile archivo() {
        return new MockMultipartFile("file", "foto.jpg", "image/jpeg", "fake-bytes".getBytes(StandardCharsets.UTF_8));
    }

    private MockMultipartFile datos(TipoPropietarioImagen tipo, Integer codigoPropietario) {
        String json = """
                {"codigo":"tmp-1","nombre":"foto","tipoPropietario":"%s","codigoPropietario":%d}
                """.formatted(tipo.name(), codigoPropietario);
        return new MockMultipartFile("datos", "", "application/json", json.getBytes(StandardCharsets.UTF_8));
    }

    private MockMultipartFile datosCliente() {
        return datos(TipoPropietarioImagen.CLIENTE, 1009000011);
    }

    private MockMultipartFile datosPelicula() {
        return datos(TipoPropietarioImagen.PELICULA, 1);
    }

    private ImagenResponse imagenDe(TipoPropietarioImagen tipo, Integer codigoPropietario) {
        return ImagenResponse.builder()
                .codigo("abc123")
                .url("https://img.test/abc123")
                .tipoPropietario(tipo.name())
                .codigoPropietario(codigoPropietario)
                .build();
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    void obtenerPublico200() throws Exception {
        ImagenResponse mock = ImagenResponse.builder().codigo("abc123").url("https://img.test/abc123").build();
        when(imagenServicio.obtener("abc123")).thenReturn(Optional.of(mock));

        MvcResult result = mockMvc.perform(get("/api/imagenes/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("abc123"))
                .andReturn();

        sout("obtenerImagenPublico200", result);
    }

    @Test
    void subirPropia201() throws Exception {
        ImagenResponse mock = ImagenResponse.builder().codigo("abc123").url("https://img.test/abc123").build();
        when(imagenServicio.registrar(any(), any())).thenReturn(mock);

        MvcResult result = mockMvc.perform(multipart("/api/imagenes")
                        .file(archivo())
                        .file(datosCliente())
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("abc123"))
                .andReturn();
        sout("subirPropia201", result);
    }

    @Test
    void subirImagenAjena403() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/imagenes")
                        .file(archivo())
                        .file(datosPelicula())
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("subirImagenAjena403", result);
    }

    @Test
    void subirAdminPelicula201() throws Exception {
        ImagenResponse mock = ImagenResponse.builder().codigo("abc123").url("https://img.test/abc123").build();
        when(imagenServicio.registrar(any(), any())).thenReturn(mock);

        MvcResult result = mockMvc.perform(multipart("/api/imagenes")
                        .file(archivo())
                        .file(datosPelicula())
                        .with(user(principalAdmin()))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn();
        sout("subirAdminPelicula201", result);
    }

    @Test
    void subirTeatroPropia201() throws Exception {
        ImagenResponse mock = ImagenResponse.builder().codigo("abc123").url("https://img.test/abc123").build();
        when(imagenServicio.registrar(any(), any())).thenReturn(mock);

        MvcResult result = mockMvc.perform(multipart("/api/imagenes")
                        .file(archivo())
                        .file(datos(TipoPropietarioImagen.ADMINISTRADOR_TEATRO, 3001))
                        .with(user(principalTeatro(3001)))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn();
        sout("subirTeatroPropia201", result);
    }

    @Test
    void subirTeatroAjena403() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/imagenes")
                        .file(archivo())
                        .file(datos(TipoPropietarioImagen.ADMINISTRADOR_TEATRO, 3002))
                        .with(user(principalTeatro(3001)))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("subirTeatroAjena403", result);
    }

    @Test
    void subirSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/imagenes")
                        .file(archivo())
                        .file(datosCliente())
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
        sout("subir401", result);
    }

    @Test
    void subirSinFile400() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/imagenes")
                        .file(datosCliente())
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
        sout("subirSinFile400", result);
    }

    @Test
    void reemplazarPropia200() throws Exception {
        when(imagenServicio.obtener("abc123"))
                .thenReturn(Optional.of(imagenDe(TipoPropietarioImagen.CLIENTE, 1009000011)));
        when(imagenServicio.actualizar(any(), any()))
                .thenReturn(imagenDe(TipoPropietarioImagen.CLIENTE, 1009000011));

        MvcResult result = mockMvc.perform(multipart(HttpMethod.PUT, "/api/imagenes/abc123")
                        .file(archivo())
                        .file(datosCliente())
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
        sout("reemplazarPropia200", result);
    }

    @Test
    void reemplazarAjena403() throws Exception {
        when(imagenServicio.obtener("abc123"))
                .thenReturn(Optional.of(imagenDe(TipoPropietarioImagen.CLIENTE, 999)));

        MvcResult result = mockMvc.perform(multipart(HttpMethod.PUT, "/api/imagenes/abc123")
                        .file(archivo())
                        .file(datosCliente())
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("reemplazarAjena403", result);
    }

    @Test
    void eliminarPropia204() throws Exception {
        when(imagenServicio.obtener("abc123"))
                .thenReturn(Optional.of(imagenDe(TipoPropietarioImagen.CLIENTE, 1009000011)));

        MvcResult result = mockMvc.perform(delete("/api/imagenes/abc123")
                        .param("confirmacion", "true")
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn();
        sout("eliminarPropia204", result);
    }

    @Test
    void eliminarAjena403() throws Exception {
        when(imagenServicio.obtener("abc123"))
                .thenReturn(Optional.of(imagenDe(TipoPropietarioImagen.CLIENTE, 999)));

        MvcResult result = mockMvc.perform(delete("/api/imagenes/abc123")
                        .param("confirmacion", "true")
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("eliminarAjena403", result);
    }

    @Test
    void eliminarAdmin204() throws Exception {
        when(imagenServicio.obtener("abc123"))
                .thenReturn(Optional.of(imagenDe(TipoPropietarioImagen.PELICULA, 1)));

        MvcResult result = mockMvc.perform(delete("/api/imagenes/abc123")
                        .param("confirmacion", "true")
                        .with(user(principalAdmin()))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn();
        sout("eliminarAdmin204", result);
    }

    @Test
    void renombrarPropiaFuerzaDueno200() throws Exception {
        when(imagenServicio.obtener("abc123"))
                .thenReturn(Optional.of(imagenDe(TipoPropietarioImagen.CLIENTE, 1009000011)));
        when(imagenServicio.renombrar(any(), eq("nuevo")))
                .thenReturn(imagenDe(TipoPropietarioImagen.CLIENTE, 1009000011));

        MvcResult result = mockMvc.perform(post("/api/imagenes/abc123/renombrar")
                        .param("nombre", "nuevo")
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        ArgumentCaptor<ImagenRequest> captor = ArgumentCaptor.forClass(ImagenRequest.class);
        verify(imagenServicio).renombrar(captor.capture(), eq("nuevo"));
        assertEquals(TipoPropietarioImagen.CLIENTE, captor.getValue().getTipoPropietario());
        assertEquals(1009000011, captor.getValue().getCodigoPropietario());
        sout("renombrarPropiaFuerzaDueno200", result);
    }

    @Test
    void restaurarPropia200() throws Exception {
        when(imagenServicio.obtener("abc123"))
                .thenReturn(Optional.of(imagenDe(TipoPropietarioImagen.CLIENTE, 1009000011)));
        when(imagenServicio.restaurar(any(), eq("v1")))
                .thenReturn(imagenDe(TipoPropietarioImagen.CLIENTE, 1009000011));

        MvcResult result = mockMvc.perform(post("/api/imagenes/abc123/restaurar")
                        .param("versionId", "v1")
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
        sout("restaurarPropia200", result);
    }

    @Test
    void obtenerInexistente404() throws Exception {
        when(imagenServicio.obtener("nope")).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/imagenes/nope"))
                .andExpect(status().isNotFound())
                .andReturn();
        sout("obtener404", result);
    }

    @Test
    void listarPorPropietario200() throws Exception {
        when(imagenServicio.listar(any(), any())).thenReturn(List.of("abc123"));

        MvcResult result = mockMvc.perform(get("/api/imagenes")
                        .param("tipoPropietario", "PELICULA")
                        .param("codigoPropietario", "1"))
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

        MvcResult result = mockMvc.perform(get("/api/imagenes/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://img.test/abc123"))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assert !body.contains("ImageKit") && !body.contains("sdk");
        sout("sinSdkEnResponse", result);
    }
}
