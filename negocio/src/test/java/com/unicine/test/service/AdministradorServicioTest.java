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

import com.unicine.entity.Administrador;
import com.unicine.service.PersonaServicio;
import com.unicine.util.validation.attributes.PersonaAtributoValidator;

import jakarta.validation.ConstraintViolationException;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class AdministradorServicioTest {

    @Autowired
    private PersonaServicio<Administrador> administradorServicio;

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

        Administrador administrador = new Administrador(1002000000, "Camilo", "Esprada", "camilo@gmail.com", "Abc12345!");

        try {
            Administrador nuevo = administradorServicio.registrar(administrador);
            
            Assertions.assertEquals(1002000000, nuevo.getCedula());
            Assertions.assertNotEquals("Abc12345!", nuevo.getPassword());

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

        Administrador administrador = new Administrador(1002000011, "Camilo", "Esprada", "camilo2@gmail.com", password);

        ConstraintViolationException excepcion = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            administradorServicio.registrar(administrador);
        });

        // Recorre los errores de validacion, le pone un formato y luego los imprime en consola para verificar los errores
        String errores = excepcion.getConstraintViolations().stream()
            .map(v -> "→ " + v.getMessage()).collect(Collectors.joining("\n"));

        System.out.println("Errores de validación: '" + password + "':\n" + errores);

        Assertions.assertFalse(excepcion.getConstraintViolations().isEmpty());

        String mensajeEsperado;

        if (password.trim().isEmpty()) {
            mensajeEsperado = "no puede estar en blanco";

        } else if ("123".equals(password)) {
            mensajeEsperado = "al menos ocho caracteres";

        } else if ("Abc12345".equals(password)) {
            mensajeEsperado = "al menos un carácter especial";

        } else if ("abc12345!".equals(password)) {
            mensajeEsperado = "al menos una letra mayúscula";

        } else {
            mensajeEsperado = "al menos una letra minúscula";
        }

        Assertions.assertTrue(
            excepcion.getConstraintViolations().stream().anyMatch(v ->
                "password".equals(v.getPropertyPath().toString()) && v.getMessage().contains(mensajeEsperado)
            )
        );
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarRepetido() {

        Administrador administrador = new Administrador(1001000000, "Camilo", "Esprada", "camilo@gmail.com", "78!Kz9'Aovr1>`A5");

        try {
            administradorServicio.registrar(administrador);
            
            Assertions.assertTrue(false);

        } catch (Exception e) {

            Assertions.assertEquals("Esta cedula ya esta registrada", e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        try{
            Administrador administrador = administradorServicio.obtener(new PersonaAtributoValidator("1001000000")).orElse(null);

            administrador.setNombre("Roberto");

            System.out.println("Contrasena del administrador: " + administrador.getPassword());

            Administrador actualizado = administradorServicio.actualizar(administrador);

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
            administrador = administradorServicio.obtener(new PersonaAtributoValidator("1001000000")).orElse(null);

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

        Administrador administrador;

        PersonaAtributoValidator cedulaValidator = new PersonaAtributoValidator(cedula.toString());

        try {
            administrador = administradorServicio.obtener(cedulaValidator).orElse(null);

            Assertions.assertEquals(cedula, administrador.getCedula());

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
        try {
            administradorServicio.eliminar(administrador, true);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
        try {
            administradorServicio.obtener(cedulaValidator);

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
            Administrador administrador = administradorServicio.obtener(new PersonaAtributoValidator("1001000000")).orElse(null);

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
            List<Administrador> lista = administradorServicio.listar();

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
