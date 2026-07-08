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

import com.unicine.service.theater.CiudadServicio;
import com.unicine.transfer.dto.request.CiudadRequest;
import com.unicine.transfer.dto.response.CiudadResponse;

@SpringBootTest
@Transactional
public class CiudadServicioTest {

    @Autowired
    private CiudadServicio ciudadServicio;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        String nombre = "Garzon";

        CiudadRequest request = CiudadRequest.builder().nombre(nombre).build();

        try {
            CiudadResponse response = ciudadServicio.registrar(request);
            
            Assertions.assertEquals(nombre, response.getNombre());

            System.out.println("\n" + "Registro guardado:" + "\n" + response);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        String nombre = "Cundinamarca";

        try{
            CiudadRequest request = CiudadRequest.builder()
                    .codigo(1)
                    .nombre(nombre)
                    .build();

            CiudadResponse actualizado = ciudadServicio.actualizar(request);

            Assertions.assertEquals(nombre, actualizado.getNombre());
            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Integer codigo = 1;

        try {
            ciudadServicio.eliminar(codigo);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
        try {
            ciudadServicio.obtener(codigo).orElse(null);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer codigo = 1;

        try {
            CiudadResponse response = ciudadServicio.obtener(codigo).orElse(null);

            Assertions.assertEquals(codigo, response.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + response);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerNombres() {

        String nombre = "Bogota";

        try {
            List<CiudadResponse> ciudades = ciudadServicio.obtenerNombre(nombre);

            Assertions.assertEquals(1, ciudades.size());

            System.out.println("\n" + "Listado de registros:");

            ciudades.forEach(System.out::println);

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
            List<CiudadResponse> lista = ciudadServicio.listar();

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
        "C", // Caso menor a cuatro caracteres
        "Cartagena-", // Caracteres especiales
        "Cartagena1", // Números
    })
    @Sql("classpath:dataset.sql")
    public void validacionNombre(String nombre) {

        try{
            CiudadRequest request = CiudadRequest.builder()
                    .codigo(1)
                    .nombre(nombre)
                    .build();

            CiudadResponse actualizado = ciudadServicio.actualizar(request);

            Assertions.assertEquals(nombre, actualizado.getNombre());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "", // Caso vacío
        "   ", // Espacios en blanco
        "Cartagena-", // Caracteres especiales
        "Cartagena1", // Números
    })
    @Sql("classpath:dataset.sql")
    public void validacionListarNombres(String nombre) {

        System.out.println("\n" + nombre);

        try {
            List<CiudadResponse> ciudades = ciudadServicio.obtenerNombre(nombre);

            Assertions.assertEquals(1, ciudades.size());

            System.out.println("\n" + "Listado de registros:");

            ciudades.forEach(System.out::println);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }
}
