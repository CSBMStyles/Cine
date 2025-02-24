package com.unicine.test.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.awt.FileDialog;
import java.awt.Frame;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Window;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.beust.jcommander.Parameter;
import com.unicine.entity.Cliente;
import com.unicine.entity.Imagen;
import com.unicine.entity.Pelicula;
import com.unicine.enumeration.EstadoPelicula;
import com.unicine.enumeration.GeneroPelicula;
import com.unicine.service.ImagenServicio;
import com.unicine.service.PeliculaServicio;
import com.unicine.service.PersonaServicio;
import com.unicine.service.extend.image.ImageKitService;
import com.unicine.util.validaciones.atributos.PeliculaAtributoValidator;
import com.unicine.util.validaciones.atributos.PersonaAtributoValidator;

import io.imagekit.sdk.models.results.ResultList;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class ImagenServicioTest {

    @Autowired
    private ImagenServicio imagenServicio;

    @Autowired
    private PersonaServicio<Cliente> clienteServicio;

    @Autowired
    private PeliculaServicio peliculaServicio;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void subirImagenCliente() {

        // Creamos un mapa de imágenes
        File file = new File("C:/Users/ASUS/Pictures/Camera Roll/Perfil/Luisa-Lopez.jpg");

        Cliente cliente;

        try {
            cliente = clienteServicio.obtener(new PersonaAtributoValidator("1005000055")).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        Imagen imagen = new Imagen();
        imagen.setCliente(cliente);

        try {
            imagenServicio.registrar(imagen, file, cliente);

            System.out.println("Imagen subida: " + imagen);
            
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirImagenPelicula() {

        // Creamos un mapa de imágenes
        File file = new File("C:/Users/ASUS/Pictures/Camera Roll/Pelicula/Dragon Ball/Dragon-Ball-Super-Heroes-2.jpg");

        Pelicula pelicula;

        try {
            pelicula = peliculaServicio.obtener(new PeliculaAtributoValidator(2)).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        Imagen imagen = new Imagen();
        imagen.setPelicula(pelicula);

        try {
            imagenServicio.registrar(imagen, file, pelicula);

            System.out.println("Imagen subida: " + imagen);
            
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    /* @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {


        File imagen = new File("C:/Users/ASUS/Pictures/Camera Roll/DSC_5118.JPG");

        Pelicula pelicula;

        try{
            pelicula = peliculaServicio.obtener(new PeliculaAtributoValidator(1)).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        // Variables que el usuario modifica
        pelicula.setRestriccionEdad(20);
        pelicula.getGeneros().add(GeneroPelicula.TERROR);
        System.out.println(pelicula.getImagenes());

        String fileIdSeleccionado = "67b243a4432c476416f7ff91";

        try {

            Pelicula actualizado = peliculaServicio.actualizar(pelicula, imagen, fileIdSeleccionado);

            Assertions.assertEquals(20, actualizado.getRestriccionEdad());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarImagenes() {
        
        try {
            ResultList result = imageKitService.listarImagenes("unicine/peliculas/Pinocho");

            result.getResults().forEach(file -> {
                System.out.println("FileId: " + file.getFileId() + "\n" + "Nombre: " + file.getName());
            });

        } catch (Exception e) { throw new RuntimeException(e); }
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

        String nombre = "P";

        try {
            List<Pelicula> peliculas = peliculaServicio.obtenerNombrePeliculas(new PeliculaAtributoValidator(nombre));

            Assertions.assertEquals(2, peliculas.size());

            System.out.println("\n" + "Listado de registros:");

            peliculas.forEach(System.out::println);

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
    } */


}
