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

import com.unicine.service.theater.TeatroServicio;
import com.unicine.transfer.dto.request.TeatroRequest;
import com.unicine.transfer.dto.response.TeatroResponse;

@SpringBootTest
@Transactional
public class TeatroServicioTest {

    @Autowired
    private TeatroServicio teatroServicio;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {
        
        String direccion = "Avenida 1 # 4-6 Este";

        TeatroRequest request = TeatroRequest.builder()
                .direccion(direccion)
                .telefono("3162316812")
                .ciudadCodigo(1)
                .administradorTeatroCedula(1119000000)
                .build();

        try {
            TeatroResponse response = teatroServicio.registrar(request);
            
            Assertions.assertEquals(direccion, response.getDireccion());

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

        String telefono = "3125867145";

        try{
            TeatroRequest request = TeatroRequest.builder()
                    .codigo(1)
                    .direccion("Calle 3 # 1 A 24 Sur")
                    .telefono(telefono)
                    .ciudadCodigo(1)
                    .administradorTeatroCedula(1119000000)
                    .build();

            TeatroResponse actualizado = teatroServicio.actualizar(request);

            Assertions.assertEquals(telefono, actualizado.getTelefono());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Integer codigo = 1;

        try {
            teatroServicio.eliminar(codigo, true);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            teatroServicio.obtener(codigo);

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
            TeatroResponse response = teatroServicio.obtener(codigo).orElse(null);

            Assertions.assertEquals(codigo, response.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + response);

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
            List<TeatroResponse> lista = teatroServicio.listar();

            Assertions.assertEquals(6, lista.size());

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
        "  ", // Espacios en blanco
        "C", // Caso menor a un caracter
        "Calle 3 # 1 A 24 Sur", // Existente
    })
    @Sql("classpath:dataset.sql")
    public void validacionDireccion(String direcion) {

        System.out.println("\n" + direcion);
        try{
            TeatroRequest request = TeatroRequest.builder()
                    .codigo(3)
                    .direccion(direcion)
                    .telefono("3162316812")
                    .ciudadCodigo(1)
                    .administradorTeatroCedula(1119000000)
                    .build();

            TeatroResponse actualizado = teatroServicio.actualizar(request);

            Assertions.assertEquals(direcion, actualizado.getDireccion());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }
}
