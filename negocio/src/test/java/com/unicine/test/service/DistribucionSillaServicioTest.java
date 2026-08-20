package com.unicine.test.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.unicine.service.theater.DistribucionSillaServicio;
import com.unicine.transfer.dto.request.DistribucionSillaRequest;
import com.unicine.transfer.dto.response.DistribucionSillaResponse;

@SpringBootTest
@Transactional
public class DistribucionSillaServicioTest {

    @Autowired
    private DistribucionSillaServicio distribucionServicio;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        String[][] matriz = {
            { " ", " ", "D", "D", "D", "D", "D", "D", " ", " " },
            { " ", "D", "D", "D", "D", "D", "D", "D", "D", " " },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" }
        };
        
        Gson gson = new Gson();
        String esquema = gson.toJson(matriz);

        DistribucionSillaRequest request = DistribucionSillaRequest.builder()
                .esquema(esquema)
                .totalSillas(100)
                .filas(10)
                .columnas(10)
                .build();

        try {
            DistribucionSillaResponse response = distribucionServicio.registrar(request);
            
            Assertions.assertEquals(esquema, response.getEsquema());

            System.out.println("\n" + "Registro guardado:" + "\n" + response);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.fail(e);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        String[][] matriz = {
            { " ", " ", "D", "D", "D", "D", "D", "D", " ", " " },
            { " ", "D", "D", "D", "D", "D", "D", "D", "D", " " },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" },
            { "D", "D", "D", "D", "D", "D", "D", "D", "D", "D" }
        };

        Gson gson = new Gson();
        String esquema = gson.toJson(matriz);

        try{
            DistribucionSillaRequest request = DistribucionSillaRequest.builder()
                    .codigo(1)
                    .esquema(esquema)
                    .totalSillas(100)
                    .filas(10)
                    .columnas(10)
                    .build();

            DistribucionSillaResponse actualizado = distribucionServicio.actualizar(request);

            Assertions.assertEquals(esquema, actualizado.getEsquema());

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
            distribucionServicio.eliminar(codigo, true);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            distribucionServicio.obtener(codigo);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        try {
            DistribucionSillaResponse distribucion = distribucionServicio.obtener(1).orElse(null);

            Assertions.assertEquals(1, distribucion.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + distribucion);

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
            List<DistribucionSillaResponse> lista = distribucionServicio.listar();

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

}
