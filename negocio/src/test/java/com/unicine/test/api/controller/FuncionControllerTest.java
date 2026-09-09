package com.unicine.test.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.FuncionController;
import com.unicine.service.purchase.EntradaServicio;
import com.unicine.service.showing.FuncionServicio;
import com.unicine.transfer.dto.response.CiudadResponse;
import com.unicine.transfer.dto.response.DetalleSillaResponse;
import com.unicine.transfer.dto.response.FuncionResponse;
import com.unicine.transfer.dto.response.HorarioResponse;
import com.unicine.transfer.dto.response.PeliculaResponse;
import com.unicine.transfer.dto.response.SalaResponse;
import com.unicine.transfer.dto.response.TeatroResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para FuncionController — cartelera publica + sillas.
 */
@WebMvcTest(controllers = FuncionController.class)
@Import(SecurityConfig.class)
class FuncionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FuncionServicio funcionServicio;

    @MockitoBean
    private EntradaServicio entradaServicio;

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    void listarFuncionesPublico200() throws Exception {
        when(funcionServicio.listar()).thenReturn(List.of(
                FuncionResponse.builder().codigo(1).precio(15000.0).build()));

        MvcResult result = mockMvc.perform(get("/api/funciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();

        sout("listarFuncionesPublico200", result);
    }

    @Test
    void obtenerFuncionPorId200() throws Exception {
        when(funcionServicio.obtener(1)).thenReturn(Optional.of(
                FuncionResponse.builder().codigo(1).precio(15000.0).build()));

        MvcResult result = mockMvc.perform(get("/api/funciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(1))
                .andReturn();

        sout("obtenerFuncion200", result);
    }

    @Test
    void obtenerFuncionNoExiste404() throws Exception {
        when(funcionServicio.obtener(999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/funciones/999"))
                .andExpect(status().isNotFound())
                .andReturn();

        sout("obtenerFuncion404", result);
    }

    @Test
    void sillasOcupadas200() throws Exception {
        when(funcionServicio.obtener(1)).thenReturn(Optional.of(
                FuncionResponse.builder().codigo(1).build()));
        when(entradaServicio.obtenerSillasOcupadas(1)).thenReturn(List.of(
                new DetalleSillaResponse(1, 1, 1)));

        MvcResult result = mockMvc.perform(get("/api/funciones/1/sillas-ocupadas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filaEntrada").value(1))
                .andReturn();

        sout("sillasOcupadas200", result);
    }

    @Test
    void sillasOcupadasFuncionNoExiste404() throws Exception {
        when(funcionServicio.obtener(999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/funciones/999/sillas-ocupadas"))
                .andExpect(status().isNotFound())
                .andReturn();

        sout("sillasOcupadas404", result);
    }

    // SECTION: Filtros de cartelera

    private FuncionResponse funcion(Integer codigo, Integer pelicula, Integer ciudad, String fechaInicio) {
        return FuncionResponse.builder()
                .codigo(codigo)
                .precio(15000.0)
                .pelicula(pelicula == null ? null : PeliculaResponse.builder().codigo(pelicula).build())
                .sala(SalaResponse.builder()
                        .codigo(10)
                        .teatro(TeatroResponse.builder()
                                .codigo(20)
                                .ciudad(ciudad == null ? null : CiudadResponse.builder().codigo(ciudad).build())
                                .build())
                        .build())
                .horario(fechaInicio == null ? null : HorarioResponse.builder()
                        .codigo(30)
                        .fechaInicio(java.time.LocalDateTime.parse(fechaInicio))
                        .fechaFin(java.time.LocalDateTime.parse(fechaInicio).plusHours(2))
                        .build())
                .build();
    }

    @Test
    void filtrarPorCiudad200() throws Exception {
        when(funcionServicio.listar()).thenReturn(List.of(
                funcion(1, 100, 1, "2026-09-10T18:00:00"),
                funcion(2, 100, 2, "2026-09-10T20:00:00")));

        MvcResult result = mockMvc.perform(get("/api/funciones").param("ciudad", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();

        sout("filtrarPorCiudad200", result);
    }

    @Test
    void filtrarPorFecha200() throws Exception {
        when(funcionServicio.listar()).thenReturn(List.of(
                funcion(1, 100, 1, "2026-09-10T18:00:00"),
                funcion(2, 100, 1, "2026-09-11T18:00:00")));

        MvcResult result = mockMvc.perform(get("/api/funciones").param("fecha", "2026-09-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value(2))
                .andReturn();

        sout("filtrarPorFecha200", result);
    }

    @Test
    void filtrosCombinadosVacios200() throws Exception {
        when(funcionServicio.listar()).thenReturn(List.of(
                funcion(1, 100, 1, "2026-09-10T18:00:00")));

        MvcResult result = mockMvc.perform(get("/api/funciones")
                        .param("pelicula", "999")
                        .param("ciudad", "1")
                        .param("fecha", "2026-09-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andReturn();

        sout("filtrosCombinadosVacios200", result);
    }

    @Test
    void fechaInvalida400() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/funciones").param("fecha", "no-es-fecha"))
                .andExpect(status().isBadRequest())
                .andReturn();

        sout("fechaInvalida400", result);
    }

    @Test
    void ordenarDescendente200() throws Exception {
        when(funcionServicio.listar()).thenReturn(List.of(
                funcion(1, 100, 1, "2026-09-10T18:00:00"),
                funcion(2, 100, 1, "2026-09-10T20:00:00")));

        MvcResult result = mockMvc.perform(get("/api/funciones")
                        .param("sort", "codigo")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(2))
                .andExpect(jsonPath("$[1].codigo").value(1))
                .andReturn();

        sout("ordenarDescendente200", result);
    }

    // !SECTION
}
