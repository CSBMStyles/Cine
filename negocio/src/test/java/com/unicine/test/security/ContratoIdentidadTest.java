package com.unicine.test.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicine.transfer.dto.response.ClienteResponse;

/**
 * Verificacion de contrato de identidad: nunca serializar password.
 */
class ContratoIdentidadTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void clienteResponseNoSerializaPassword() throws Exception {
        ClienteResponse response = ClienteResponse.builder()
                .cedula(1009000011)
                .nombre("Pepe")
                .correo("pepe@test.com")
                .estado(true)
                .build();

        String json = mapper.writeValueAsString(response);

        System.out.println("\n>>> serializacion ClienteResponse | json=" + json + "\n");

        assertThat(json).doesNotContain("password");
        assertThat(json).contains("pepe@test.com");
        assertThat(json).contains("1009000011");
    }

    @Test
    void passwordHashNoDebeLoggearse() {
        // Regresion: @ToString.Exclude en Persona.password evita que el hash salga en logs
        // Verificacion indirecta: ClienteResponse.toString() no contiene password
        ClienteResponse response = ClienteResponse.builder().correo("a@b.com").build();
        String toString = response.toString();
        System.out.println("\n>>> toString sin password: " + toString + "\n");
        assertThat(toString).doesNotContain("password");
    }
}
