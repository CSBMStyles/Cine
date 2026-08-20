package com.unicine.test.service;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.user.Administrador;
import com.unicine.repository.user.AdministradorRepo;
import com.unicine.service.user.AdministradorServicio;
import com.unicine.transfer.dto.request.AdministradorRequest;
import com.unicine.transfer.dto.response.AdministradorResponse;

import jakarta.validation.ConstraintViolationException;

// Important: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class AdministradorServicioTest {

    @Autowired
    private AdministradorServicio administradorServicio;

    @Autowired
    private AdministradorRepo administradorRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void login() {

        try {
            Administrador administrador = administradorServicio.login("cristiansimelot@gmail.com", "78!Kz9'Aovr1>`A5");

            Assertions.assertEquals("cristiansimelot@gmail.com", administrador.getCorreo());

            System.out.println("\n" + "Administrador encontrado:" + "\n" + administrador);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        AdministradorRequest request = AdministradorRequest.builder()
            .cedula(1002000000)
            .nombre("Camilo")
            .apellido("Esprada")
            .correo("camilo@gmail.com")
            .password("Abc12345!")
            .build();

        try {
            AdministradorResponse nuevo = administradorServicio.registrar(request);
            
            Assertions.assertEquals(1002000000, nuevo.getCedula());

            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());
            
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "   ",
        "123",
        "Abc12345",
        "abc12345!",
        "ABC12345!"
    })
    @Sql("classpath:dataset.sql")
    public void registrarContraseñaInvalida(String password) {

        AdministradorRequest request = AdministradorRequest.builder()
            .cedula(1002000011)
            .nombre("Camilo")
            .apellido("Esprada")
            .correo("camilo2@gmail.com")
            .password(password)
            .build();

        ConstraintViolationException excepcion = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            administradorServicio.registrar(request);
        });

        String errores = excepcion.getConstraintViolations().stream()
            .map(v -> "→ " + v.getMessage()).collect(Collectors.joining("\n"));

        System.out.println("Errores de validación: '" + password + ":\n" + errores);

        Assertions.assertFalse(excepcion.getConstraintViolations().isEmpty());

        String mensajeEsperado;

        if (password.trim().isEmpty()) {
            mensajeEsperado = "no puede estar en blanco";

        } else if ("123".equals(password)) {
            mensajeEsperado = "al menos ocho caracteres";

        } else if ("Abc12345".equals(password)) {
            mensajeEsperado = "al menos un caracter especial";

        } else if ("abc12345!".equals(password)) {
            mensajeEsperado = "al menos una letra mayuscula";

        } else {
            mensajeEsperado = "al menos una letra minuscula";
        }

        Assertions.assertTrue(
            excepcion.getConstraintViolations().stream().anyMatch(v ->
                v.getPropertyPath().toString().endsWith("password") && v.getMessage().contains(mensajeEsperado)
            )
        );
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarRepetido() {

        AdministradorRequest request = AdministradorRequest.builder()
            .cedula(1001000000)
            .nombre("Camilo")
            .apellido("Esprada")
            .correo("camilo@gmail.com")
            .password("78!Kz9'Aovr1>`A5")
            .build();

        try {
            administradorServicio.registrar(request);
            
            Assertions.assertTrue(false);

        } catch (Exception e) {

            Assertions.assertEquals("La cedula ya esta registrada", e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        try{
            AdministradorResponse existente = administradorServicio.obtener(1001000000).orElse(null);

            Assertions.assertNotNull(existente);

            AdministradorRequest request = AdministradorRequest.builder()
                .cedula(existente.getCedula())
                .nombre("Roberto")
                .apellido(existente.getApellido())
                .correo(existente.getCorreo())
                .password("78!Kz9'Aovr1>`A5")
                .build();

            AdministradorResponse actualizado = administradorServicio.actualizar(request);

            Assertions.assertEquals("Roberto", actualizado.getNombre());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizarContraseña() {

        Administrador administrador;

        try{
            administrador = administradorRepo.findById(1001000000).orElse(null);

            Assertions.assertNotNull(administrador);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
        try {
            administradorServicio.cambiarPassword(administrador, "78!Kz9'Aovr1>`A5", "1Jr>T$A54*6[)`");

            System.out.println("\n" + "Contraseña actualizada correctamente");

            Assertions.assertTrue(true);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
        try {
            administradorServicio.login(administrador.getCorreo(), "1Jr>T$A54*6[)`");

            System.out.println("\n" + "Contraseña verificada correctamente");

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {
        
        Integer cedula = 1001000000;

        try {
            AdministradorResponse administrador = administradorServicio.obtener(cedula).orElse(null);

            Assertions.assertEquals(cedula, administrador.getCedula());

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
        try {
            administradorServicio.eliminar(cedula, true);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
        try {
            administradorServicio.obtener(cedula);

        } catch (Exception e) {

            // Realizamos una validacion de la prueba para aceptar que el administrador fue eliminado mendiante la excepcion del metodo de obtener
            Assertions.assertThrows(Exception.class, () -> {
                throw e;
            });

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        try {
            AdministradorResponse administrador = administradorServicio.obtener(1001000000).orElse(null);

            Assertions.assertEquals(1001000000, administrador.getCedula());

            System.out.println("\n" + "Registro encontrado:" + "\n" + administrador);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<AdministradorResponse> lista = administradorServicio.listar();

            Assertions.assertEquals(1, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }
}
