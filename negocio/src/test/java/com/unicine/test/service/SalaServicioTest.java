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

import com.unicine.enums.theater.TipoSala;
import com.unicine.service.theater.SalaServicio;
import com.unicine.transfer.dto.request.SalaRequest;
import com.unicine.transfer.dto.response.SalaResponse;

@SpringBootTest
@Transactional
public class SalaServicioTest {

    @Autowired
    private SalaServicio salaServicio;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {
        
        SalaRequest request = SalaRequest.builder()
                .nombre("ARX-01")
                .tipoSala(TipoSala.valueOf("DX4"))
                .teatroCodigo(1)
                .distribucionSillaCodigo(1)
                .build();

        try {
            SalaResponse response = salaServicio.registrar(request);
            
            Assertions.assertEquals("ARX-01", response.getNombre());

            System.out.println("\n" + "Registro guardado:" + "\n" + response);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        String nombre = "P-01: Premier Dorada";

        try{
            SalaRequest request = SalaRequest.builder()
                    .codigo(1)
                    .nombre(nombre)
                    .tipoSala(TipoSala.DX4)
                    .teatroCodigo(1)
                    .distribucionSillaCodigo(1)
                    .build();

            SalaResponse actualizado = salaServicio.actualizar(request);

            Assertions.assertEquals(nombre, actualizado.getNombre());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        try {
            salaServicio.eliminar(1, true);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }

        try {
            salaServicio.obtener(1);

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
            SalaResponse sala = salaServicio.obtener(codigo).orElse(null);

            Assertions.assertEquals(codigo, sala.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + sala);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<SalaResponse> lista = salaServicio.listar();

            Assertions.assertEquals(8, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }

    }

    // 🟥

    @ParameterizedTest
    @ValueSource(strings = {
        "", // Caso vacío
        "  ", // Espacios en blanco
        "Atl", // Caso menor a un caracter
        "Atlantis", // Existente
    })
    @Sql("classpath:dataset.sql")
    public void validacionNombres(String nombre) {

        System.out.println("\n" + nombre);
        try{
            List<SalaResponse> salas = salaServicio.obtenerNombresTeatro(nombre, 5);

            Assertions.assertEquals(1, salas.size());

            System.out.println("\n" + "Registros:" + "\n" + salas);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }
}
