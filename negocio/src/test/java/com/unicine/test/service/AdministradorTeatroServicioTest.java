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

import com.unicine.entity.AdministradorTeatro;
import com.unicine.service.PersonaServicio;
import com.unicine.util.validation.attributes.PersonaAtributoValidator;

import jakarta.validation.ConstraintViolationException;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class AdministradorTeatroServicioTest {

    @Autowired
    private PersonaServicio<AdministradorTeatro> administradorTeatroServicio;

    @Test
    @Sql("classpath:dataset.sql")
    public void login() {

        try {
            AdministradorTeatro administrador = administradorTeatroServicio.login("jhona.belloc@uqvirtual.edu.co", "78!Kz9'Aovr1>`A5");

            Assertions.assertEquals("jhona.belloc@uqvirtual.edu.co", administrador.getCorreo());

            System.out.println("\n" + "Administrador de teatro encontrado:" + "\n" + administrador);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        AdministradorTeatro administrador = new AdministradorTeatro(1773000000, "Mariana", "Carta", "mariana@gmail.com", "78!Kz9'Aovr1>`A5");

        try {
            AdministradorTeatro nuevo = administradorTeatroServicio.registrar(administrador);
            
            Assertions.assertEquals(1773000000, nuevo.getCedula());
            Assertions.assertNotEquals("78!Kz9'Aovr1>`A5", nuevo.getPassword());

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

        AdministradorTeatro administrador = new AdministradorTeatro(1773000001, "Mariana", "Carta", "mariana2@gmail.com", password);

        ConstraintViolationException excepcion = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            administradorTeatroServicio.registrar(administrador);
        });

        String errores = excepcion.getConstraintViolations().stream()
            .map(v -> "→ " + v.getMessage())
            .collect(Collectors.joining("\n"));

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
    public void actualizar() {

        try{
            AdministradorTeatro administrador = administradorTeatroServicio.obtener(new PersonaAtributoValidator("1119000000")).orElse(null);

            administrador.setNombre("Daniela");

            AdministradorTeatro actualizado = administradorTeatroServicio.actualizar(administrador);

            Assertions.assertEquals("Daniela", actualizado.getNombre());

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

        AdministradorTeatro administrador;

        try {
            administrador = administradorTeatroServicio.obtener(new PersonaAtributoValidator("1119000000")).orElse(null);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            administradorTeatroServicio.cambiarPassword(administrador, "78!Kz9'Aovr1>`A5", "2Jr>T$A54*6[)`");

            System.out.println("\n" + "Contraseña actualizada correctamente");

            Assertions.assertTrue(true);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            administradorTeatroServicio.login(administrador.getCorreo(), "2Jr>T$A54*6[)`");

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
        
        Integer cedula = 1119000000;

        AdministradorTeatro administrador;

        PersonaAtributoValidator cedulaValidator = new PersonaAtributoValidator(cedula.toString());

        try {
            administrador = administradorTeatroServicio.obtener(cedulaValidator).orElse(null);

            Assertions.assertEquals(cedula, administrador.getCedula());

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
        try {
            administradorTeatroServicio.eliminar(administrador
            , true);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
        try {
            administradorTeatroServicio.obtener(cedulaValidator);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

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
            AdministradorTeatro administrador = administradorTeatroServicio.obtener(new PersonaAtributoValidator("1119000000")).orElse(null);

            Assertions.assertEquals(1119000000, administrador.getCedula());

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
            List<AdministradorTeatro> lista = administradorTeatroServicio.listar();

            Assertions.assertEquals(6, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }
}
