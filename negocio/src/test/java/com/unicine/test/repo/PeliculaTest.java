package com.unicine.test.repo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unicine.dto.DetallePeliculaHorarioDTO;
import com.unicine.entidades.EstadoPelicula;
import com.unicine.entidades.GeneroPelicula;
import com.unicine.entidades.Pelicula;
import com.unicine.repo.PeliculaRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PeliculaTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado */

    @Autowired
    private PeliculaRepo peliculaRepo;

    private static Logger logger;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        // Creamos un mapa de imágenes
        Map<String, String> imagenes = new HashMap<>();
        imagenes.put("http://example.com/imagen-1.jpg", "perfil");

        // Crear la lista de generos
        List<GeneroPelicula> generos = new ArrayList<>();
        generos.add(GeneroPelicula.ACCION);

        // Crear la lista de reparto
        List<String> repartos = new ArrayList<>();
        repartos.add("David Howard Thornton");
        repartos.add("Jenna Kanell");
        
        EstadoPelicula estado = EstadoPelicula.EN_CARTELERA;
        Pelicula pelicula = new Pelicula(estado, generos, imagenes, "Terrifier", repartos, "En la noche de Halloween, tras una fiesta, Tara y Dawn entran en una pizzería. Tras ellas llega un payaso inquietante y grotesco que hiela la sangre a Tara. Las chicas no tardan en descubrir que es un psicópata sádico que pretende matarlas.", "https://youtu.be/UOrNESb8T4I?si=lMhpWAgNXeelOsrz", 3.9, 18);
        pelicula.setCodigo(6);

        Pelicula guardado = peliculaRepo.save(pelicula);

        Assertions.assertNotNull(guardado);

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Pelicula guardado = peliculaRepo.findById(1).orElse(null);

        System.out.println(guardado);

        guardado.setPuntuacion(4.2);

        Pelicula actualizado = peliculaRepo.save(guardado);

        Assertions.assertEquals(4.2, actualizado.getPuntuacion());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Pelicula buscado = peliculaRepo.findById(1).orElse(null);

        System.out.println(buscado);

        peliculaRepo.delete(buscado);

        Pelicula verificacion = peliculaRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<Pelicula> buscado = peliculaRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        logger = LoggerFactory.getLogger(PeliculaTest.class);

        List<Pelicula> peliculas = peliculaRepo.findAll();

        Assertions.assertEquals(5, peliculas.size());

        // NOTE: En este caso se usa el logger para imprimir el resultado ya que si se usar el sout para mostrarlo omite las tildes y caracteres especiales usando el ? en lugar de la letra, cuando se muestrs en la consola debug
        for (Pelicula p : peliculas) {
            logger.info("Pelicula: ", p);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Pelicula> peliculas = peliculaRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, peliculas.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (Pelicula p : peliculas) {
            System.out.println(p);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<Pelicula> peliculas = peliculaRepo.findAll(Sort.by("nombre"));

        Assertions.assertEquals(5, peliculas.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (Pelicula p : peliculas) {
            System.out.println(p);
        }
    }

    // SECTION: Consultas personalizadas para la base de datos

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerPeliculaNombre() {

        Optional<Pelicula> buscado = peliculaRepo.obtenerPeliculaNombre("Encanto");

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido por nombre:");

        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerPeliculaFuncion() {

        Optional<Pelicula> buscado = peliculaRepo.obtenerPeliculaFuncion(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido por funcion:");

        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void buscarPeliculasFuncion() {

        List<Object[]> peliculas = peliculaRepo.buscarPeliculasFuncion("Encan");

        Assertions.assertEquals(1, peliculas.size());

        System.out.println("\n" + "Listado de registros obtenidos por funcion:");

        peliculas.forEach(o -> {
            System.out.println("Pelicula: " + o[0] + "\n" + "Funcion: " + o[1]);
        });
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerPeliculaFecha() {

        List<Object[]> peliculas = peliculaRepo.obtenerPeliculaFecha(1, LocalDate.of(2024, 12, 14));

        Assertions.assertEquals(1, peliculas.size());

        System.out.println("\n" + "Listado de registros obtenidos por fecha:");

        peliculas.forEach(o -> {
            System.out.println("Pelicula: " + o[0] + "\n" + "Funcion: " + o[1] + "\n" + "Horario: " + o[2]);
        });
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPeliculasCuidadEstado() {

        List<Pelicula> peliculas = peliculaRepo.listarPeliculasCuidadEstado(1, EstadoPelicula.EN_CARTELERA);

        Assertions.assertEquals(1, peliculas.size());

        System.out.println("\n" + "Listado de registros obtenidos por ciudad y estado:");

        for (Pelicula p : peliculas) {
            System.out.println(p);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPeliculasEstado() {

        List<Pelicula> peliculas = peliculaRepo.listarPeliculasEstado(EstadoPelicula.EN_CARTELERA);

        Assertions.assertEquals(3, peliculas.size());

        System.out.println("\n" + "Listado de registros obtenidos por estado:");

        for (Pelicula p : peliculas) {
            System.out.println(p);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPeliculasNombreCuidad() {

        List<Pelicula> peliculas = peliculaRepo.listarPeliculasNombreCuidad("o", 2);

        Assertions.assertEquals(2, peliculas.size());

        System.out.println("\n" + "Listado de registros obtenidos por nombre y ciudad:");

        for (Pelicula p : peliculas) {
            System.out.println(p);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void peliculaHorariosSalas() {

        List<DetallePeliculaHorarioDTO> peliculas = peliculaRepo.peliculaHorariosSalas(1, 2);

        Assertions.assertEquals(1, peliculas.size());

        System.out.println("\n" + "Listado de registros obtenidos por pelicula, horario y sala:");

        for (DetallePeliculaHorarioDTO p : peliculas) {
            System.out.println(p);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void peliculaTaquilleraCiudad() {

        List<Object[]> peliculas = peliculaRepo.peliculaTaquilleraCiudad(2);

        Assertions.assertEquals(2, peliculas.size());

        System.out.println("\n" + "Listado de registros obtenidos:");

        peliculas.forEach(o -> {
            System.out.println("Pelicula: " + o[0] + "\n" + "Cantidad de compras: " + o[1]);
        });
    }
}
