package com.unicine.test.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.user.Cliente;
import com.unicine.entity.user.Administrador;
import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.entity.user.Persona;
import com.unicine.repository.user.ClienteRepo;
import com.unicine.service.user.ClienteServicio;
import com.unicine.service.user.AuthenticationService;
import com.unicine.service.notification.EmailService;
import com.unicine.transfer.dto.request.ClienteRequest;
import com.unicine.transfer.dto.response.ClienteResponse;

import jakarta.validation.ConstraintViolationException;

// Important: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class ClienteServicioTest {

    @Autowired
    private ClienteServicio clienteServicio;

    @Autowired
    private ClienteRepo clienteRepo;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private EmailService emailService;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void login() {

        String correo = "pepe@hotmail.com";

        try {
            Cliente cliente = clienteServicio.login(correo, "78!Kz9'Aovr1>`A5");

            Assertions.assertEquals(correo, cliente.getCorreo());

            System.out.println("\n" + "Cliente encontrado:" + "\n" + cliente);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "pepe@hotmail.com,78!Kz9'Aovr1>`A5",
        "cristiansimelot@gmail.com,78!Kz9'Aovr1>`A5",
        "cristians.barreram@uqvirtual.edu.co,78!Kz9'Aovr1>`A5"
    })
    @Sql("classpath:dataset.sql")
    public void loginUsuariosMultiples(String correo, String password) {
        try {
            Persona usuario = authService.login(correo, password);

            Assertions.assertNotNull(usuario);
            Assertions.assertEquals(correo, usuario.getCorreo());

            Assertions.assertTrue(
                usuario instanceof Cliente
                    || usuario instanceof Administrador
                    || usuario instanceof AdministradorTeatro,
                "Tipo de usuario no esperado: " + usuario.getClass().getSimpleName()
            );

            System.out.println("Login exitoso: " + correo + " -> " + usuario.getClass().getSimpleName());
            
        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.fail("Falló la autenticación: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Integer cedula = 1004000066;

        ArrayList<String> telefonos = new ArrayList<>();
        telefonos.add("3160369165");

        LocalDate fechaNacimiento = LocalDate.of(1990, 10, 10);

        ClienteRequest request = ClienteRequest.builder()
            .cedula(cedula)
            .nombre("Juan")
            .apellido("Parra")
            .correo("juan@gmail.com")
            .password("78!Kz9'Aovr1>`A5")
            .estado(false)
            .fechaNacimiento(fechaNacimiento)
            .telefonos(telefonos)
            .build();

        try {
            ClienteResponse nuevo = clienteServicio.registrar(request);
            
            Assertions.assertEquals(cedula, nuevo.getCedula());

            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(true);

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

        ArrayList<String> telefonos = new ArrayList<>();
        telefonos.add("3160369165");

        ClienteRequest request = ClienteRequest.builder()
            .cedula(1004000077)
            .nombre("Juan")
            .apellido("Parra")
            .correo("juan2@gmail.com")
            .password(password)
            .estado(true)
            .fechaNacimiento(LocalDate.of(1990, 10, 10))
            .telefonos(telefonos)
            .build();

        ConstraintViolationException excepcion = Assertions.assertThrows(ConstraintViolationException.class, () -> {
            clienteServicio.registrar(request);
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
            ClienteResponse existente = clienteServicio.obtener(1009000011).orElse(null);

            Assertions.assertNotNull(existente);

            ClienteRequest request = ClienteRequest.builder()
                .cedula(existente.getCedula())
                .nombre(existente.getNombre())
                .apellido(existente.getApellido())
                .correo("josefinas@gmail.com")
                .password("78!Kz9'Aovr1>`A5")
                .estado(existente.getEstado())
                .fechaNacimiento(existente.getFechaNacimiento())
                .telefonos(existente.getTelefonos())
                .build();

            ClienteResponse actualizado = clienteServicio.actualizar(request);

            Assertions.assertEquals(true, actualizado.getEstado());
            Assertions.assertEquals("josefinas@gmail.com", actualizado.getCorreo());

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

        Cliente cliente;

        try {
            cliente = clienteRepo.findById(1009000011).orElse(null);

            Assertions.assertNotNull(cliente);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            clienteServicio.cambiarPassword(cliente, "78!Kz9'Aovr1>`A5", "2Jr>T$A54*6[)`");

            System.out.println("\n" + "Contraseña actualizada correctamente");

            Assertions.assertTrue(true);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            clienteServicio.login(cliente.getCorreo(), "2Jr>T$A54*6[)`");

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
        
        Integer cedula = 1009000011;

        try {
            ClienteResponse cliente = clienteServicio.obtener(cedula).orElse(null);

            Assertions.assertEquals(cedula, cliente.getCedula());

            System.out.println("\n" + "Registro encontrado:" + "\n" + cliente);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
        try {
            clienteServicio.eliminar(cedula, true);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
        try {
            clienteServicio.obtener(cedula);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

    
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer cedula = 1009000011;

        try {
            ClienteResponse cliente = clienteServicio.obtener(cedula).orElse(null);

            Assertions.assertEquals(cedula, cliente.getCedula());

            System.out.println("\n" + "Registro encontrado:" + "\n" + cliente);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<ClienteResponse> lista = clienteServicio.listar();

            Assertions.assertEquals(5, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    // 🟥

    @ParameterizedTest
    @ValueSource(strings = {
        "", // Caso vacío
        "   ", // Espacios en blanco
        "juan@outlook.com", // Correo existente
        "correo@dominio", // Falta el dominio
        "correo@.com", // Dominio incorrecto
        "@dominio.com", // Falta el nombre de usuario
        "correo!#@dominio.com", // Caracteres especiales no permitidos
        "correo@dominio..com", // Dominio con puntos consecutivos
    })
    @Sql("classpath:dataset.sql")
    public void validacionCorreo(String correo) {

        System.out.println("Correo: " + correo);

        try{
            ClienteResponse existente = clienteServicio.obtener(1009000011).orElse(null);

            Assertions.assertNotNull(existente);

            ClienteRequest request = ClienteRequest.builder()
                .cedula(existente.getCedula())
                .nombre(existente.getNombre())
                .apellido(existente.getApellido())
                .correo(correo)
                .password("78!Kz9'Aovr1>`A5")
                .estado(existente.getEstado())
                .fechaNacimiento(existente.getFechaNacimiento())
                .telefonos(existente.getTelefonos())
                .build();

            ClienteResponse actualizado = clienteServicio.actualizar(request);

            Assertions.assertEquals(correo, actualizado.getCorreo());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void validacionEstado() {

        try{
            ClienteResponse existente = clienteServicio.obtener(1009000011).orElse(null);

            Assertions.assertNotNull(existente);

            ClienteRequest request = ClienteRequest.builder()
                .cedula(existente.getCedula())
                .nombre(existente.getNombre())
                .apellido(existente.getApellido())
                .correo(existente.getCorreo())
                .password("78!Kz9'Aovr1>`A5")
                .estado(null)
                .fechaNacimiento(existente.getFechaNacimiento())
                .telefonos(existente.getTelefonos())
                .build();

            ClienteResponse actualizado = clienteServicio.actualizar(request);

            Assertions.assertEquals(null, actualizado.getEstado());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    /**
     * Prueba para actualizar el telefono de un cliente, pero superando el limite de caracteres
     */
    @ParameterizedTest
    @ValueSource(strings = {"123456789", "12345678900", "-123456789", "123456789+", "123456789x"})
    @Sql("classpath:dataset.sql")
    public void validacionTelefono(String telefono) {

        ArrayList<String> telefonos = new ArrayList<>();
        telefonos.add(telefono);

        try{
            ClienteResponse existente = clienteServicio.obtener(1009000011).orElse(null);

            Assertions.assertNotNull(existente);

            ClienteRequest request = ClienteRequest.builder()
                .cedula(existente.getCedula())
                .nombre(existente.getNombre())
                .apellido(existente.getApellido())
                .correo(existente.getCorreo())
                .password("78!Kz9'Aovr1>`A5")
                .estado(existente.getEstado())
                .fechaNacimiento(existente.getFechaNacimiento())
                .telefonos(telefonos)
                .build();

            ClienteResponse actualizado = clienteServicio.actualizar(request);

            Assertions.assertEquals(1, actualizado.getTelefonos().size());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void enviarCorreo() {

        emailService.enviarEmail("Prueba de correo", "Este es un correo de prueba", "cristianbarrera100@gmail.com");
    }
}
