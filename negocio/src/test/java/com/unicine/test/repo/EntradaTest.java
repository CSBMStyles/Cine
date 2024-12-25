package com.unicine.test.repo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;
import java.util.Optional;

import com.unicine.dto.DetalleSillaDTO;
import com.unicine.entidades.Compra;
import com.unicine.entidades.Entrada;
import com.unicine.repo.CompraRepo;
import com.unicine.repo.EntradaRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EntradaTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperada */

    @Autowired
    private EntradaRepo entradaRepo;

    @Autowired
    private CompraRepo compraRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Compra compra = compraRepo.findById(1).orElse(null);

        Entrada entrada = new Entrada(5000.00, 5, 2, compra);
        entrada.setCodigo(7);

        Entrada guardado = entradaRepo.save(entrada);

        Assertions.assertNotNull(guardado);

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Entrada guardado = entradaRepo.findById(1).orElse(null);

        System.out.println(guardado);

        guardado.setPrecio(6000.00);

        Entrada actualizado = entradaRepo.save(guardado);

        Assertions.assertEquals(6000, actualizado.getPrecio());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Entrada buscado = entradaRepo.findById(1).orElse(null);

        System.out.println(buscado);

        entradaRepo.delete(buscado);

        Entrada verificacion = entradaRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<Entrada> buscado = entradaRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<Entrada> entradas = entradaRepo.findAll();

        Assertions.assertEquals(6, entradas.size());

        System.out.println("\n" + "Listado de registros:");

        for (Entrada e : entradas) {
            System.out.println(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Entrada> entradas = entradaRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, entradas.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (Entrada e : entradas) {
            System.out.println(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<Entrada> entradas = entradaRepo.findAll(Sort.by("codigo").ascending());

        Assertions.assertEquals(6, entradas.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (Entrada e : entradas) {
            System.out.println(e);
        }
    }

    // SECTION: Consultas personalizadas para la base de datos

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerEntradasCompra() {
        List<Entrada> entradas = entradaRepo.obtenerEntradasCompra(1);

        Assertions.assertEquals(1, entradas.size());

        System.out.println("Resultado: \n");

        entradas.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerSillasOcupadas() {
        List<DetalleSillaDTO> sillas = entradaRepo.obtenerSillasOcupadas(2);

        Assertions.assertEquals(1, sillas.size());

        System.out.println("Resultado: \n");

        sillas.forEach(System.out::println);
    } 
}
