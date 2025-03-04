package com.unicine.test.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.Ciudad;
import com.unicine.service.CiudadServicio;
import com.unicine.util.validation.attributes.CiudadAtributoValidator;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class CiudadServicioTest {

    @Autowired
    private CiudadServicio ciudadServicio;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        String nombre = "Garzon";

        Ciudad ciudad = new Ciudad(nombre);

        try {
            Ciudad nuevo = ciudadServicio.registrar(ciudad);
            
            Assertions.assertEquals(nombre, nuevo.getNombre());

            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        String nombre = "Cundinamarca";

        try{
            Ciudad ciudad = ciudadServicio.obtener(new CiudadAtributoValidator(1)).orElse(null);

            ciudad.setNombre(nombre);

            Ciudad actualizado = ciudadServicio.actualizar(ciudad);

            Assertions.assertEquals(nombre, actualizado.getNombre());
            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Integer codigo = 1;

        Ciudad ciudad;

        CiudadAtributoValidator validator = new CiudadAtributoValidator(codigo);

        try {
            ciudad = ciudadServicio.obtener(validator).orElse(null);
        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            ciudadServicio.eliminar(ciudad);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
        try {
            ciudadServicio.obtener(validator).orElse(null);

        } catch (Exception e) {
            // Realizamos una validacion de la prueba para aceptar que el ciudad fue eliminado mendiante la excepcion del metodo de obtener
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer codigo = 1;

        try {
            Ciudad ciudad = ciudadServicio.obtener(new CiudadAtributoValidator(codigo)).orElse(null);

            Assertions.assertEquals(codigo, ciudad.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + ciudad);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerNombres() {

        String nombre = "Bogota";

        try {
            List<Ciudad> ciudades = ciudadServicio.obtenerNombre(new CiudadAtributoValidator(nombre));

            Assertions.assertEquals(1, ciudades.size());

            System.out.println("\n" + "Listado de registros:");

            ciudades.forEach(System.out::println);

        } catch (Exception e) {
            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Ciudad> lista = ciudadServicio.listar();

            Assertions.assertEquals(5, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    // 🟥

    @ParameterizedTest
    @ValueSource(strings = {
        "", // Caso vacío
        "   ", // Espacios en blanco
        "C", // Caso menor a cuatro caracteres
        "Cartagena-", // Caracteres especiales
        "Cartagena1", // Números
    })
    @Sql("classpath:dataset.sql")
    public void validacionNombre(String nombre) {

        try{
            Ciudad ciudad = ciudadServicio.obtener(new CiudadAtributoValidator(1)).orElse(null);

            ciudad.setNombre(nombre);

            Ciudad actualizado = ciudadServicio.actualizar(ciudad);

            Assertions.assertEquals(nombre, actualizado.getNombre());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "", // Caso vacío
        "   ", // Espacios en blanco
        "C", // Caso menor a cuatro caracteres
        "Cartagena-", // Caracteres especiales
        "Cartagena1", // Números
    })
    @Sql("classpath:dataset.sql")
    public void validacionListarNombres(String nombre) {

        System.out.println("\n" + nombre);

        try {
            List<Ciudad> ciudades = ciudadServicio.obtenerNombre(new CiudadAtributoValidator(nombre));

            Assertions.assertEquals(1, ciudades.size());

            System.out.println("\n" + "Listado de registros:");

            ciudades.forEach(System.out::println);

        } catch (Exception e) {
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }
}
