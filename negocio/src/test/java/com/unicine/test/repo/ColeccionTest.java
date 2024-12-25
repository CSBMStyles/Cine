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

import com.unicine.entidades.Cliente;
import com.unicine.entidades.Pelicula;
import com.unicine.entidades.Coleccion;
import com.unicine.entidades.ColeccionComposicion;
import com.unicine.entidades.EstadoPropio;
import com.unicine.repo.ClienteRepo;
import com.unicine.repo.ColeccionRepo;
import com.unicine.repo.PeliculaRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ColeccionTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado */

    @Autowired
    private ColeccionRepo coleccionRepo;

    @Autowired
    private ClienteRepo clienteRepo;

    @Autowired
    private PeliculaRepo peliculaRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Cliente cliente = clienteRepo.findById(1006000044).orElse(null);

        Pelicula pelicula = peliculaRepo.findById(1).orElse(null);
        
        Coleccion coleccion = new Coleccion(2.0, EstadoPropio.FAVORITO, cliente, pelicula);

        Coleccion guardado = coleccionRepo.save(coleccion);

        Assertions.assertNotNull(guardado);

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        ColeccionComposicion id = new ColeccionComposicion(1009000011, 1);

        Coleccion guardado = coleccionRepo.findById(id).orElse(null);

        System.out.println(guardado);

        guardado.setEstadoPeliculaPropio(EstadoPropio.valueOf("FAVORITO"));

        Coleccion actualizado = coleccionRepo.save(guardado);

        Assertions.assertEquals("FAVORITO", guardado.getEstadoPeliculaPropio().toString());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        ColeccionComposicion id = new ColeccionComposicion(1009000011, 1);

        Coleccion buscado = coleccionRepo.findById(id).orElse(null);

        System.out.println(buscado);

        coleccionRepo.delete(buscado);

        Coleccion verificacion = coleccionRepo.findById(id).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        ColeccionComposicion id = new ColeccionComposicion(1009000011, 1);

        Optional<Coleccion> buscado = coleccionRepo.findById(id);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<Coleccion> colecciones = coleccionRepo.findAll();

        Assertions.assertEquals(5, colecciones.size());

        System.out.println("\n" + "Listado de registros:");

        for (Coleccion c : colecciones) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Coleccion> colecciones = coleccionRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, colecciones.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (Coleccion c : colecciones) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<Coleccion> colecciones = coleccionRepo.findAll(Sort.by("puntuacion"));

        Assertions.assertEquals(5, colecciones.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (Coleccion c : colecciones) {
            System.out.println(c);
        }
    }
}
