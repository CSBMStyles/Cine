package com.unicine.test.servicio;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.unicine.entidades.DistribucionSilla;
import com.unicine.servicio.DistribucionSillaServicio;
import com.unicine.util.validacion.atributos.DistribucionAtributoValidator;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

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
        String json = gson.toJson(matriz);

        DistribucionSilla distribucion = new DistribucionSilla(json, null, 0, 0, 0);

        try {
            DistribucionSilla nuevo = distribucionServicio.registrar(distribucion);
            
            Assertions.assertEquals(json, nuevo.getEsquema());

            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) {
            Assertions.assertTrue(true);

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
        String json = gson.toJson(matriz);

        try{
            DistribucionSilla distribucion = distribucionServicio.obtener(new DistribucionAtributoValidator("1")).orElse(null);

            System.out.println("\n" + "Registro antigüo:" + "\n" + distribucion);

            distribucion.setEsquema(json);

            DistribucionSilla actualizado = distribucionServicio.actualizar(distribucion);

            Assertions.assertEquals(json, actualizado.getEsquema());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Integer codigo = 1;

        DistribucionSilla distribucion;

        DistribucionAtributoValidator distribucionCodigo = new DistribucionAtributoValidator(codigo.toString());

        try {
            distribucion = distribucionServicio.obtener(distribucionCodigo).orElse(null);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            distribucionServicio.eliminar(distribucion, true);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            distribucionServicio.obtener(distribucionCodigo);

        } catch (Exception e) {
            // Realizamos una validacion de la prueba para aceptar que el distribucion fue eliminado mendiante la excepcion del metodo de obtener
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        try {
            DistribucionSilla distribucion = distribucionServicio.obtener(new DistribucionAtributoValidator("1")).orElse(null);

            Assertions.assertEquals(5, distribucion.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + distribucion);

        } catch (Exception e) {
            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<DistribucionSilla> lista = distribucionServicio.listar();

            Assertions.assertEquals(5, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    // 🟥

}
