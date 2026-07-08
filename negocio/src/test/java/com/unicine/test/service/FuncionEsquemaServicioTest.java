package com.unicine.test.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.unicine.service.showing.FuncionEsquemaServicio;
import com.unicine.service.showing.FuncionServicio;
import com.unicine.transfer.dto.request.FuncionEsquemaRequest;
import com.unicine.transfer.dto.response.FuncionEsquemaResponse;
import com.unicine.transfer.dto.response.FuncionResponse;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class FuncionEsquemaServicioTest {

    @Autowired
    private FuncionEsquemaServicio funcionEsquemaServicio;

    @Autowired
    private FuncionServicio funcionServicio;

    private final Gson gson = new Gson();

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        FuncionResponse funcion;

        try {
            funcion = funcionServicio.obtener(8).orElse(null);

            System.out.println("Funcion encontrada: " + funcion); 

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());



            throw new RuntimeException(e);

        }

        FuncionEsquemaRequest funcionEsquemaRequest = FuncionEsquemaRequest.builder()
                .funcionCodigo(funcion.getCodigo())
                .disponibles(0)
                .ocupadas(0)
                .mantenimiento(0)
                .build();

        try {
            FuncionEsquemaResponse nuevo = funcionEsquemaServicio.registrar(funcionEsquemaRequest);

            Assertions.assertEquals(78, nuevo.getDisponibles());

            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());



            throw new RuntimeException(e);

        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        FuncionEsquemaResponse funcionEsquema = null;

        String[][] matriz = null;

        int x = 2;
        int y = 4;

        try {

            funcionEsquema = funcionEsquemaServicio.obtener(1).orElse(null);

            // Modifica la matriz de la disposicion de la funcion
            matriz = gson.fromJson(funcionEsquema.getEsquemaTemporal(), String[][].class);

            matriz[x][y] = "O";

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());



            throw new RuntimeException(e);

        }
        
        try {

            FuncionEsquemaRequest request = FuncionEsquemaRequest.builder()
                    .codigo(funcionEsquema.getCodigo())
                    .esquemaTemporal(gson.toJson(matriz))
                    .ocupadas(funcionEsquema.getOcupadas())
                    .disponibles(funcionEsquema.getDisponibles())
                    .mantenimiento(funcionEsquema.getMantenimiento())
                    .funcionCodigo(funcionEsquema.getFuncionCodigo())
                    .build();

            FuncionEsquemaResponse actualizado = funcionEsquemaServicio.actualizar(request);

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
            funcionEsquemaServicio.eliminar(1, true);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());



            throw new RuntimeException(e);

        }

        try {
            funcionEsquemaServicio.obtener(1);

        } catch (Exception e) { 

            Assertions.assertThrows(Exception.class, () -> {throw e;}); 

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        try {
            FuncionEsquemaResponse funcionEsquema = funcionEsquemaServicio.obtener(1).orElse(null);

            Assertions.assertEquals(1, funcionEsquema.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + funcionEsquema);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());



            throw new RuntimeException(e);

        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<FuncionEsquemaResponse> lista = funcionEsquemaServicio.listar();

            Assertions.assertEquals(7, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());



            throw new RuntimeException(e);

        }
    }
}
