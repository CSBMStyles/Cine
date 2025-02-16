package com.unicine.test.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.unicine.entity.Funcion;
import com.unicine.entity.FuncionEsquema;
import com.unicine.service.FuncionEsquemaServicio;
import com.unicine.service.FuncionServicio;

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

        Funcion funcion;

        try {
            funcion = funcionServicio.obtener(8).orElse(null);

            System.out.println("Funcion encontrada: " + funcion); 

        } catch (Exception e) { throw new RuntimeException(e); }

        FuncionEsquema funcionEsquema = new FuncionEsquema(funcion);

        try {
            FuncionEsquema nuevo = funcionEsquemaServicio.registrar(funcionEsquema);

            Assertions.assertEquals(78, nuevo.getDisponibles());

            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        FuncionEsquema funcionEsquema;

        int x = 2;
        int y = 4;

        try {

            funcionEsquema = funcionEsquemaServicio.obtener(1).orElse(null);

            // Modifica la matriz de la disposicion de la funcion
            String[][] matriz = gson.fromJson(funcionEsquema.getEsquemaTemporal(), String[][].class);

            matriz[x][y] = "O";

            funcionEsquema.setEsquemaTemporal(gson.toJson(matriz));

        } catch (Exception e) { throw new RuntimeException(e); }
        
        try {

            FuncionEsquema actualizado = funcionEsquemaServicio.actualizar(funcionEsquema);

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        FuncionEsquema funcionEsquema;

        try {
            funcionEsquema = funcionEsquemaServicio.obtener(1).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            funcionEsquemaServicio.eliminar(funcionEsquema, true);

        } catch (Exception e) { throw new RuntimeException(e); }

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
            FuncionEsquema funcionEsquema = funcionEsquemaServicio.obtener(1).orElse(null);

            Assertions.assertEquals(1, funcionEsquema.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + funcionEsquema);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<FuncionEsquema> lista = funcionEsquemaServicio.listar();

            Assertions.assertEquals(7, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
