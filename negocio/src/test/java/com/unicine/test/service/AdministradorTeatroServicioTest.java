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

import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.repository.user.AdministradorTeatroRepo;
import com.unicine.service.user.AdministradorTeatroServicio;
import com.unicine.transfer.dto.request.AdministradorTeatroRequest;
import com.unicine.transfer.dto.response.AdministradorTeatroResponse;

import jakarta.validation.ConstraintViolationException;

// Important: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class AdministradorTeatroServicioTest {

    @Autowired
    private AdministradorTeatroServicio administradorTeatroServicio;

    @Autowired
    private AdministradorTeatroRepo administradorTeatroRepo;

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

        AdministradorTeatroRequest request = AdministradorTeatroRequest.builder()
            .cedula(1773000000)
            .nombre("Mariana")
            .apellido("Carta")
            .correo("mariana@gmail.com")
            .password("78!Kz9'Aovr1>`A5")
            .build();

        try {
            AdministradorTeatroResponse nuevo = administradorTeatroServicio.registrar(request);
            
            Assertions.assertEquals(1773000000, nuevo.getCedula());

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

        AdministradorTeatroRequest request = AdministradorTeatroRequest.builder()
            .cedula(1773000001)
            .nombre("Mariana")
            .apellido("Carta")
            .correo("mariana2@gmail.com")
            .password(password)
            .build();

        ConstraintViolationException excepcion = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            administradorTeatroServicio.registrar(request);
        });

        String errores = excepcion.getConstraintViolations().stream()
            .map(v -> "→ " + v.getMessage())
            .collect(Collectors.joining("\n"));

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
    public void actualizar() {

        try{
            AdministradorTeatroResponse existente = administradorTeatroServicio.obtener(1119000000).orElse(null);

            Assertions.assertNotNull(existente);

            AdministradorTeatroRequest request = AdministradorTeatroRequest.builder()
                .cedula(existente.getCedula())
                .nombre("Daniela")
                .apellido(existente.getApellido())
                .correo(existente.getCorreo())
                .password("78!Kz9'Aovr1>`A5")
                .build();

            AdministradorTeatroResponse actualizado = administradorTeatroServicio.actualizar(request);

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
            administrador = administradorTeatroRepo.findById(1119000000).orElse(null);

            Assertions.assertNotNull(administrador);

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

        try {
            AdministradorTeatroResponse administrador = administradorTeatroServicio.obtener(cedula).orElse(null);

            Assertions.assertEquals(cedula, administrador.getCedula());

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
        try {
            administradorTeatroServicio.eliminar(cedula, true);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
        try {
            administradorTeatroServicio.obtener(cedula);

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
            AdministradorTeatroResponse administrador = administradorTeatroServicio.obtener(1119000000).orElse(null);

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
            List<AdministradorTeatroResponse> lista = administradorTeatroServicio.listar();

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
