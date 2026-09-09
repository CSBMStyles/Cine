package com.unicine.test.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Prueba de contexto SpringDoc (tarea 4.6): la especificacion OpenAPI
 * se genera al arrancar e incluye rutas, esquema ApiError y seguridad.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void especificacionIncluyeRutasNucleo() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("UniCine API"))
                .andExpect(jsonPath("$.paths./api/compras").exists())
                .andExpect(jsonPath("$.paths./api/funciones").exists())
                .andExpect(jsonPath("$.paths./api/imagenes").exists())
                .andExpect(jsonPath("$.paths./api/comentarios").exists())
                .andExpect(jsonPath("$.paths./api/cupones").exists())
                .andReturn();

        System.out.println("\n>>> openapi paths=" + result.getResponse().getContentAsString().length() + " chars\n");
    }

    @Test
    void especificacionIncluyeApiErrorYSeguridad() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.schemas.ApiError").exists())
                .andExpect(jsonPath("$.components.schemas.ApiError.properties.code").exists())
                .andExpect(jsonPath("$.components.schemas.ApiError.properties.details").exists())
                .andExpect(jsonPath("$.components.securitySchemes.bearer-jwt").exists())
                .andReturn();
    }
}
