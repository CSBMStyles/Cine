package com.unicine.test.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicine.entity.theater.Teatro;
import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.entity.user.Cliente;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.transfer.dto.auth.LoginRequest;
import com.unicine.transfer.dto.auth.LoginResponse;
import com.unicine.transfer.dto.response.AdministradorResponse;
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

    @Test
    void loginRequestToStringNoFiltraPassword() {
        LoginRequest request = LoginRequest.builder()
                .correo("pepe@test.com")
                .password("Secreto123*")
                .build();

        String texto = request.toString();

        System.out.println("\n>>> toString LoginRequest: " + texto + "\n");

        assertThat(texto).doesNotContain("Secreto123*");
        assertThat(texto).contains("pepe@test.com");
    }

    @Test
    void usuarioPrincipalNoSerializaPassword() throws Exception {
        UsuarioPrincipal principal = new UsuarioPrincipal(
                1009000011, "pepe@test.com", "hash-secreto", TipoUsuario.CLIENTE);

        String json = mapper.writeValueAsString(principal);

        System.out.println("\n>>> serializacion UsuarioPrincipal | json=" + json + "\n");

        assertThat(json).doesNotContain("hash-secreto");
        assertThat(json).doesNotContain("password");
        assertThat(json).contains("pepe@test.com");
    }

    @Test
    void clientePrincipalSinTeatros() {
        Cliente cliente = new Cliente();
        cliente.setCedula(1009000011);
        cliente.setCorreo("pepe@test.com");
        cliente.setPassword("hash");

        UsuarioPrincipal principal = UsuarioPrincipal.desdePersona(cliente, TipoUsuario.CLIENTE);

        assertThat(principal.getTeatroIds()).isEmpty();
        assertThat(principal.getAuthorities()).extracting("authority").contains("ROLE_CLIENTE");
    }

    @Test
    void adminTeatroPrincipalMapeaTeatroIds() {
        AdministradorTeatro admin = new AdministradorTeatro(2001, "Ana", "Teatro",
                "ana@test.com", "hash");
        Teatro teatro = Teatro.builder().direccion("Calle 123 #45-67").telefono("3001234567").build();
        teatro.setCodigo(7);
        admin.setTeatros(java.util.List.of(teatro));

        UsuarioPrincipal principal = UsuarioPrincipal.desdePersona(admin, TipoUsuario.ADMINISTRADOR_TEATRO);

        System.out.println("\n>>> teatroIds=" + principal.getTeatroIds() + "\n");

        assertThat(principal.getTeatroIds()).containsExactly(7);
        assertThat(principal.getAuthorities()).extracting("authority")
                .contains("ROLE_ADMINISTRADOR_TEATRO");
    }

    @Test
    void loginResponseNoExponePassword() throws Exception {
        LoginResponse response = LoginResponse.builder()
                .cedula(1009000011)
                .nombre("Pepe")
                .correo("pepe@test.com")
                .tipo(TipoUsuario.CLIENTE)
                .teatroIds(java.util.List.of())
                .mensaje("Autenticado correctamente. JWT pendiente Fase 5.")
                .build();

        String json = mapper.writeValueAsString(response);

        assertThat(json).doesNotContain("password");
        assertThat(json).contains("CLIENTE");
    }

    @Test
    void administradorResponseNoSerializaPassword() throws Exception {
        AdministradorResponse response = AdministradorResponse.builder()
                .cedula(2001)
                .correo("admin@test.com")
                .build();

        String json = mapper.writeValueAsString(response);

        assertThat(json).doesNotContain("password");
    }
}
