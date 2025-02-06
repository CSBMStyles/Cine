package com.unicine.test.repo;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import com.google.gson.Gson;
import com.unicine.entidades.DistribucionSilla;
import com.unicine.repo.DistribucionSillaRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DistribucionSillaTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado */

    @Autowired
    private DistribucionSillaRepo distribucionSillaRepo;

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

        DistribucionSilla distribucionSilla = new DistribucionSilla(esquema);
        distribucionSilla.setCodigo(6);

        DistribucionSilla guardado = distribucionSillaRepo.save(distribucionSilla);

        Assertions.assertEquals(esquema, guardado.getEsquema());

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        DistribucionSilla guardado = distribucionSillaRepo.findById(1).orElse(null);

        System.out.println(guardado);

        guardado.setFilas(14);

        DistribucionSilla actualizado = distribucionSillaRepo.save(guardado);

        Assertions.assertEquals(14, actualizado.getFilas());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        DistribucionSilla buscado = distribucionSillaRepo.findById(1).orElse(null);

        System.out.println(buscado);

        distribucionSillaRepo.delete(buscado);

        DistribucionSilla verificacion = distribucionSillaRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<DistribucionSilla> buscado = distribucionSillaRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<DistribucionSilla> distribucionSillas = distribucionSillaRepo.findAll();

        Assertions.assertEquals(5, distribucionSillas.size());

        System.out.println("\n" + "Listado de registros:");

        for (DistribucionSilla c : distribucionSillas) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<DistribucionSilla> distribucionSillas = distribucionSillaRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, distribucionSillas.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (DistribucionSilla c : distribucionSillas) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<DistribucionSilla> distribucionSillas = distribucionSillaRepo.findAll(Sort.by("totalSillas").ascending());

        Assertions.assertEquals(5, distribucionSillas.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (DistribucionSilla c : distribucionSillas) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerDistribucionFuncion() {

       String buscado = distribucionSillaRepo.obtenerEsquemaFuncion(1);

        Assertions.assertNotNull(buscado);

        Gson gson = new Gson();

        String[][] matriz = gson.fromJson(buscado, String[][].class);

        for (String[] fila : matriz) {
            for (String silla : fila) {
                System.out.print(silla + " ");
            }
            System.out.println();
        }

        System.out.println("\n" + "Registro obtenido:" + "\n" + buscado);

    }
}
