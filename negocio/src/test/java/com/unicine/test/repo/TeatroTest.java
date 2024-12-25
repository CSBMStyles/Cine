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

import com.unicine.entidades.AdministradorTeatro;
import com.unicine.entidades.Ciudad;
import com.unicine.entidades.Teatro;
import com.unicine.repo.AdministradorTeatroRepo;
import com.unicine.repo.CiudadRepo;
import com.unicine.repo.TeatroRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TeatroTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado */

    @Autowired
    private TeatroRepo teatroRepo;

    @Autowired
    private CiudadRepo ciudadRepo;

    @Autowired
    private AdministradorTeatroRepo adminRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Ciudad ciudad = ciudadRepo.findById(1).orElse(null);

        AdministradorTeatro admin = adminRepo.findById(1).orElse(null);
        
        Teatro teatro = new Teatro("Carrera 15 # 1-2 Centro", "3162594316", ciudad, admin);
        teatro.setCodigo(7);

        Teatro guardado = teatroRepo.save(teatro);

        Assertions.assertNotNull(guardado);

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Teatro guardado = teatroRepo.findById(1).orElse(null);

        System.out.println(guardado);

        guardado.setTelefono("3145093154");

        Teatro actualizado = teatroRepo.save(guardado);

        Assertions.assertEquals("3145093154", guardado.getTelefono());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Teatro buscado = teatroRepo.findById(1).orElse(null);

        System.out.println(buscado);

        teatroRepo.delete(buscado);

        Teatro verificacion = teatroRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<Teatro> buscado = teatroRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<Teatro> teatros = teatroRepo.findAll();

        Assertions.assertEquals(6, teatros.size());

        System.out.println("\n" + "Listado de registros:");

        for (Teatro t : teatros) {
            System.out.println(t);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Teatro> teatros = teatroRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, teatros.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (Teatro t : teatros) {
            System.out.println(t);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<Teatro> teatros = teatroRepo.findAll(Sort.by("codigo"));

        Assertions.assertEquals(6, teatros.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (Teatro t : teatros) {
            System.out.println(t);
        }
    }

    // SECTION: Consultas personalizadas para la base de datos

    @Test
    @Sql("classpath:dataset.sql")
    public void listarTeatrosCiudad() {

        List<Teatro> teatros = teatroRepo.listarTeatrosCiudad("Armenia");

        Assertions.assertEquals(1, teatros.size());

        System.out.println("\n" + "Listado de teatros por ciudad:");

        for (Teatro t : teatros) {
            System.out.println(t);
        }
    }
}
