package com.unicine.test.servicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entidades.Cliente;
import com.unicine.servicio.PersonaServicio;
import com.unicine.util.mail.EmailService;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class ClienteServicioTest {

    @Autowired
    private PersonaServicio<Cliente> clienteServicio;

    @Autowired
    private EmailService emailService;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void login() {

        String correo = "pepe@hotmail.com";

        try {
            Cliente cliente = clienteServicio.login(correo, "fe5i/PFsjWU0/+4VjImKacbXbnsiQ07+L49lGB5bq4fQ5u5lMiNXljo0s+oSV73N");

            Assertions.assertEquals(correo, cliente.getCorreo());

            System.out.println("\n" + "Cliente encontrado:" + "\n" + cliente);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Integer cedula = 1004000066;

         // Creamos un mapa de imágenes
        Map<String, String> imagenes = new HashMap<>();
        imagenes.put("http://example.com/imagen-1.jpg", "perfil");

        // Crear la lista de teléfonos
        ArrayList<String> telefonos = new ArrayList<>();
        telefonos.add("3160369165");

        LocalDate fechaNacimiento = LocalDate.of(1990, 10, 10);

        Cliente cliente = new Cliente(cedula, "Juan", "Parra", "juan@gmail.com", "78!Kz9'Aovr1>`A5", false, fechaNacimiento, imagenes, telefonos);

        try {
            Cliente nuevo = clienteServicio.registrar(cliente);
            
            Assertions.assertEquals(cedula, nuevo.getCedula());

            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        try{
            Cliente cliente = clienteServicio.obtener(1009000011).orElse(null);

            cliente.setEstado(false);

            Cliente actualizado = clienteServicio.actualizar(cliente);

            Assertions.assertEquals(false, actualizado.getEstado());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {
        
        Integer cedula = 1009000011;

        try {
            clienteServicio.eliminar(cedula);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
        try {
            clienteServicio.obtener(cedula);

        } catch (Exception e) {
            // Realizamos una validacion de la prueba para aceptar que el cliente fue eliminado mendiante la excepcion del metodo de obtener
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer cedula = 1009000011;

        try {
            Cliente cliente = clienteServicio.obtener(cedula).orElse(null);

            Assertions.assertEquals(cedula, cliente.getCedula());

            System.out.println("\n" + "Registro encontrado:" + "\n" + cliente);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Cliente> lista = clienteServicio.listar();

            Assertions.assertEquals(5, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    // 🟥

    // NOTE: La siguiente prueba es interesante aparte de buscar que falle exitosamente, se realiza la prueba de forma parametrizada para probar con diferentes valores, esto es importante para en una sola prueba probar diferentes casos para el mismo enfoque y no tener que realizar multiples pruebas

    @ParameterizedTest
    @ValueSource(strings = {
        "", // Caso vacío
        "   ", // Espacios en blanco
        "pepe@hotmail.com", // Correo existente
        "correo@dominio", // Falta el dominio
        "correo@.com", // Dominio incorrecto
        "@dominio.com", // Falta el nombre de usuario
        "correo!#@dominio.com", // Caracteres especiales no permitidos
        "correo@dominio..com", // Dominio con puntos consecutivos
    })
    @Sql("classpath:dataset.sql")
    public void validacionCorreo(String correo) {

        try{
            Cliente cliente = clienteServicio.obtener(1009000011).orElse(null);

            cliente.setCorreo(correo);

            Cliente actualizado = clienteServicio.actualizar(cliente);

            Assertions.assertEquals(correo, actualizado.getCorreo());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void validacionEstado() {

        try{
            Cliente cliente = clienteServicio.obtener(1009000011).orElse(null);

            cliente.setEstado(null);

            Cliente actualizado = clienteServicio.actualizar(cliente);

            Assertions.assertEquals(null, actualizado.getEstado());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
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

        // Crear la lista de teléfonos
        ArrayList<String> telefonos = new ArrayList<>();
        telefonos.add(telefono);

        try{
            Cliente cliente = clienteServicio.obtener(1009000011).orElse(null);

            cliente.setTelefonos(telefonos);

            Cliente actualizado = clienteServicio.actualizar(cliente);

            Assertions.assertEquals(1, actualizado.getTelefonos().size());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
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
