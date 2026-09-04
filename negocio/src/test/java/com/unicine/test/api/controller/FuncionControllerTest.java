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
import com.unicine.transfer.dto.response.DetalleSillaResponse;
import com.unicine.transfer.dto.response.FuncionResponse;
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
}
