package com.unicine.test.repo;

import java.time.LocalDateTime;
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

import com.unicine.entidades.Cupon;
import com.unicine.repo.CuponRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CuponTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado */

    @Autowired
    private CuponRepo cuponRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        LocalDateTime fechaVencimiento = LocalDateTime.of(2024, 12, 31, 23, 59);

        Cupon cupon = new Cupon("Cupon del 15% de descuento por aniversario", 0.15, "Aniversario uno", fechaVencimiento);
        cupon.setCodigo(3);

        Cupon guardado = cuponRepo.save(cupon);

        Assertions.assertEquals("Aniversario uno", guardado.getCriterio());

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Cupon guardado = cuponRepo.findById(1).orElse(null);

        System.out.println(guardado);

        guardado.setCriterio("Pareja");

        Cupon actualizado = cuponRepo.save(guardado);

        Assertions.assertEquals("Pareja", actualizado.getCriterio());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Cupon buscado = cuponRepo.findById(1).orElse(null);

        System.out.println(buscado);

        cuponRepo.delete(buscado);

        Cupon verificacion = cuponRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<Cupon> buscado = cuponRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<Cupon> cupones = cuponRepo.findAll();

        Assertions.assertEquals(2, cupones.size());

        System.out.println("\n" + "Listado de registros:");

        for (Cupon c : cupones) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Cupon> cupones = cuponRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(2, cupones.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (Cupon c : cupones) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<Cupon> cupones = cuponRepo.findAll(Sort.by("criterio"));

        Assertions.assertEquals(2, cupones.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (Cupon c : cupones) {
            System.out.println(c);
        }
    }
}
