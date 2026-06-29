package com.unicine.test.service;

import com.unicine.entity.user.Administrador;
import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.entity.user.Cliente;
import com.unicine.entity.user.Persona;
import com.unicine.exception.AuthenticationException;
import com.unicine.service.user.AuthenticationService;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tests dedicados para AuthenticationService.
 * Cubre la cascada de login (cliente -> administrador -> administrador de teatro),
 * el rechazo de credenciales invalidas y la activacion de la validacion @NotBlank
 * introducida en el refactor de la tarea 2.9.
 */
@SpringBootTest
@Transactional
public class AuthenticationServiceTest {

    private static final String PASSWORD_VALIDA = "78!Kz9'Aovr1>`A5";
    private static final String PASSWORD_INVALIDA = "ContrasenaIncorrecta123!";

    @Autowired
    private AuthenticationService authService;

    @Test
    @Sql("classpath:dataset.sql")
    public void loginCliente() {
        Persona persona = authService.login("pepe@hotmail.com", PASSWORD_VALIDA);

        Assertions.assertNotNull(persona);
        Assertions.assertInstanceOf(Cliente.class, persona);
        Assertions.assertEquals("pepe@hotmail.com", persona.getCorreo());
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void loginAdministrador() {
        Persona persona = authService.login("cristiansimelot@gmail.com", PASSWORD_VALIDA);

        Assertions.assertNotNull(persona);
        Assertions.assertInstanceOf(Administrador.class, persona);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void loginAdministradorTeatro() {
        Persona persona = authService.login("jhona.belloc@uqvirtual.edu.co", PASSWORD_VALIDA);

        Assertions.assertNotNull(persona);
        Assertions.assertInstanceOf(AdministradorTeatro.class, persona);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void loginCredencialesInvalidasLanzaAuthenticationException() {
        // El correo existe pero la contrasena no.
        Assertions.assertThrows(AuthenticationException.class,
                () -> authService.login("pepe@hotmail.com", PASSWORD_INVALIDA));

        // El correo no existe en ninguno de los 3 perfiles: tambien debe lanzar.
        Assertions.assertThrows(AuthenticationException.class,
                () -> authService.login("inexistente@dominio.com", PASSWORD_VALIDA));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void loginCorreoVacioLanzaConstraintViolation() {
        // @NotBlank en el parametro 'correo' debe activarse via @Validated.
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> authService.login("", PASSWORD_VALIDA));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void loginContrasenaVaciaLanzaConstraintViolation() {
        // @NotBlank en el parametro 'password' debe activarse via @Validated.
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> authService.login("pepe@hotmail.com", ""));
    }
}
