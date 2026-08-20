package com.unicine.test.exception.handler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.unicine.exception.handler.GlobalExceptionHandler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(controllers = ContractTestController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void respuestasExitosasUsanDTOsTipados() throws Exception {
        mockMvc.perform(get("/contract/success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("ok"));

        mockMvc.perform(post("/contract/success"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value("created"));

        mockMvc.perform(put("/contract/success/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("updated"));

        mockMvc.perform(delete("/contract/success/1"))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @CsvSource({
            "/contract/not-found,404,DOMAIN_SHOWING_ENTITY_SCHEDULE_NOT_FOUND",
            "/contract/business-rule,400,DOMAIN_SHOWING_BUSINESS_RULE_SCHEDULE_OVERLAP",
            "/contract/authentication,401,DOMAIN_USER_AUTH_INVALID_CREDENTIALS",
            "/contract/authorization,403,DOMAIN_USER_AUTH_ACTION_NOT_PERMITTED",
            "/contract/conflict,409,DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED",
            "/contract/external,502,DOMAIN_IMAGE_EXTERNAL_UPLOAD_ERROR"
    })
    void erroresDeDominioUsanEstadoYCodigoConsistentes(String endpoint, int httpStatus, String code)
            throws Exception {
        mockMvc.perform(get(endpoint))
                .andExpect(status().is(httpStatus))
                .andExpect(jsonPath("$.status").value(httpStatus))
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.path").value(endpoint))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void errorInesperadoNoExponeDetalleInterno() throws Exception {
        mockMvc.perform(get("/contract/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.code").doesNotExist())
                .andExpect(jsonPath("$.message").value("Error interno del servidor. Contacte al administrador."))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("detalle interno"))));
    }

    @Test
    void validacionDeBodyDevuelveDetallesPorCampo() throws Exception {
        mockMvc.perform(post("/contract/body")
                        .contentType(APPLICATION_JSON)
                        .content("{\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La solicitud contiene errores de validacion"))
                .andExpect(jsonPath("$.details[0].field").value("nombre"))
                .andExpect(jsonPath("$.details[0].message").value("nombre requerido"));
    }

    @Test
    void validacionDeParametroDevuelveDetallesPorParametro() throws Exception {
        mockMvc.perform(get("/contract/method").param("codigo", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0].field").value("codigo"))
                .andExpect(jsonPath("$.details[0].message").value("codigo requerido"));
    }

}
