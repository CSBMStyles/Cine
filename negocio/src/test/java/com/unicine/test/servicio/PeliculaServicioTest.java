package com.unicine.test.servicio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entidades.Pelicula;
import com.unicine.servicio.PeliculaServicio;
import com.unicine.util.emuns.EstadoPelicula;
import com.unicine.util.emuns.GeneroPelicula;
import com.unicine.util.validacion.atributos.PeliculaAtributoValidator;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class PeliculaServicioTest {

    @Autowired
    private PeliculaServicio peliculaServicio;

    // 🟩

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
        Map<String, String> repartos = new HashMap<>();
        repartos.put("Director", "Damien Leone");
        repartos.put("Terrifier", "David Howard");
        repartos.put("Tara", "Jenna Kanell");
        
        EstadoPelicula estado = EstadoPelicula.CARTELERA;
        Pelicula pelicula = new Pelicula(estado, generos, imagenes, "Terrifier", repartos, "En la noche de Halloween, tras una fiesta, Tara y Dawn entran en una pizzería. Tras ellas llega un payaso inquietante y grotesco que hiela la sangre a Tara. Las chicas no tardan en descubrir que es un psicópata sádico que pretende matarlas.", "https://youtu.be/UOrNESb8T4I?si=lMhpWAgNXeelOsrz", 3.9, 18);

        try {
            Pelicula nuevo = peliculaServicio.registrar(pelicula);
            
            Assertions.assertEquals("Terrifier", nuevo.getNombre());

            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) {
            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Integer edad = 20;

        try{
            Pelicula pelicula = peliculaServicio.obtener(new PeliculaAtributoValidator(1)).orElse(null);

            pelicula.setRestriccionEdad(edad);

            Pelicula actualizado = peliculaServicio.actualizar(pelicula);

            Assertions.assertEquals(edad, actualizado.getRestriccionEdad());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Integer codigo = 1;

        Pelicula pelicula;

        PeliculaAtributoValidator validator = new PeliculaAtributoValidator(codigo);

        try {
            pelicula = peliculaServicio.obtener(validator).orElse(null);
        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            peliculaServicio.eliminar(pelicula, true);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
        try {
            peliculaServicio.obtener(validator);

        } catch (Exception e) {
            // Realizamos una validacion de la prueba para aceptar que el pelicula fue eliminado mendiante la excepcion del metodo de obtener
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer codigo = 1;

        try {
            Pelicula pelicula = peliculaServicio.obtener(new PeliculaAtributoValidator(codigo)).orElse(null);

            Assertions.assertEquals(codigo, pelicula.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + pelicula);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerNombres() {

        String nombre = "Bogota";

        try {
            List<Pelicula> ciudades = peliculaServicio.obtenerNombrePeliculas(new PeliculaAtributoValidator(nombre));

            Assertions.assertEquals(1, ciudades.size());

            System.out.println("\n" + "Listado de registros:");

            ciudades.forEach(System.out::println);

        } catch (Exception e) {
            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Pelicula> lista = peliculaServicio.listar();

            Assertions.assertEquals(5, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }


}
