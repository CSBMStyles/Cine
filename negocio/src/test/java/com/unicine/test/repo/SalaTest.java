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

import com.unicine.entidades.DistribucionSilla;
import com.unicine.entidades.Sala;
import com.unicine.entidades.Teatro;
import com.unicine.entidades.TipoSala;
import com.unicine.repo.DistribucionSillaRepo;
import com.unicine.repo.SalaRepo;
import com.unicine.repo.TeatroRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SalaTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado */

    @Autowired
    private SalaRepo salaRepo;

    @Autowired
    private TeatroRepo teatroRepo;

    @Autowired
    private DistribucionSillaRepo distribucionSillaRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Teatro teatro = teatroRepo.findById(1).orElse(null);

        DistribucionSilla distribucionSilla = distribucionSillaRepo.findById(1).orElse(null);

        Sala sala = new Sala("Patacol", TipoSala.valueOf("XD"), teatro, distribucionSilla);
        sala.setCodigo(9);

        Sala guardado = salaRepo.save(sala);

        Assertions.assertNotNull(guardado);

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Sala guardado = salaRepo.findById(1).orElse(null);

        System.out.println(guardado);

        guardado.setNombre("Repascol");

        Sala actualizado = salaRepo.save(guardado);

        Assertions.assertEquals("Repascol", guardado.getNombre());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Sala buscado = salaRepo.findById(1).orElse(null);

        System.out.println(buscado);

        salaRepo.delete(buscado);

        Sala verificacion = salaRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<Sala> buscado = salaRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<Sala> salas = salaRepo.findAll();

        Assertions.assertEquals(8, salas.size());

        System.out.println("\n" + "Listado de registros:");

        for (Sala s : salas) {
            System.out.println(s);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Sala> salas = salaRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, salas.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (Sala s : salas) {
            System.out.println(s);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<Sala> salas = salaRepo.findAll(Sort.by("nombre"));

        Assertions.assertEquals(8, salas.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (Sala s : salas) {
            System.out.println(s);
        }
    }
}
