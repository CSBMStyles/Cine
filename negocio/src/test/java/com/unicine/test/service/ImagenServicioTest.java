package com.unicine.test.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.Cliente;
import com.unicine.entity.Imagen;
import com.unicine.entity.Pelicula;
import com.unicine.service.ImagenServicio;
import com.unicine.service.PeliculaServicio;
import com.unicine.service.PersonaServicio;
import com.unicine.service.extend.image.ImageKitService;
import com.unicine.transfer.record.VersionArchivo;
import com.unicine.util.validation.attributes.PeliculaAtributoValidator;
import com.unicine.util.validation.attributes.PersonaAtributoValidator;

import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.models.results.ResultList;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class ImagenServicioTest {

    @Autowired
    private ImagenServicio imagenServicio;

    @Autowired
    private ImageKitService imagenKitIo;

    @Autowired
    private PersonaServicio<Cliente> clienteServicio;

    @Autowired
    private PeliculaServicio peliculaServicio;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void comprobarLectura(){

        Path path = Paths.get("C:/Users/ASUS/Pictures/Camera Roll/DSC_3672 M.JPG");

        // Leer el archivo en un array de bytes
        byte[] content;

        try {
            content = Files.readAllBytes(path);

        } catch (IOException e) { throw new RuntimeException("Error leyendo el archivo", e); }

        MockMultipartFile multipartFile = new MockMultipartFile("file", path.getFileName().toString(), "image/jpg", content);

        System.out.println("Formato" + multipartFile.getName());

        System.out.println("Nombre del archivo" + multipartFile.getOriginalFilename());

        System.out.println("Tipo de contenido" + multipartFile.getContentType());
    }

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
            /* clienteServicio.validarImagen(cliente.getCedula()); */

            imagenServicio.registrar(imagen, file, cliente);

            System.out.println("Imagen subida: " + imagen);
            
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirImagenPelicula() {

        // Creamos un mapa de imágenes
        File file = new File("C:/Users/ASUS/Pictures/Camera Roll/DSC_3672 M.JPG");

        Pelicula pelicula;

        try {
            pelicula = peliculaServicio.obtener(new PeliculaAtributoValidator(5)).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        Imagen imagen = new Imagen();

        imagen.setPelicula(pelicula);

        try {
            imagenServicio.registrar(imagen, file, pelicula);

            System.out.println("Imagen subida: " + imagen);
            
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {


        File file = new File("C:/Users/ASUS/Pictures/Camera Roll/DSC_5118.jpg");

        // NOTE: Listamos las imagenes de la pelicula y seleccionamos el fileId de la imagen que queremos actualizar

        Imagen imagenAntigua;

        String fileIdSeleccionado = "67cca0f3432c47641676174c";

        try {
            imagenAntigua = imagenServicio.obtener(fileIdSeleccionado).orElse(null);

            Assertions.assertNotNull(imagenAntigua, "La imagen antigua no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagenAntigua);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {

            Imagen actualizado = imagenServicio.actualizar(imagenAntigua, file, imagenAntigua.getPelicula());

            Assertions.assertEquals(fileIdSeleccionado, actualizado.getCodigo());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void recuperarVersion(){

        Imagen imagenAntigua;

        String fileIdSeleccionado = "67cca0f3432c47641676174c";

        // NOTE: Listamos las versiones de la imagen y seleccionamos el versionId de la version que queremos recuperar
        
        String versionIdSeleccionado = "----";

        try {
            imagenAntigua = imagenServicio.obtener(fileIdSeleccionado).orElse(null);

            Assertions.assertNotNull(imagenAntigua, "La imagen antigua no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagenAntigua);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {

            Imagen imagenRecuperada = imagenServicio.restaurar(imagenAntigua, versionIdSeleccionado);

            System.out.println("\n" + "Registro recuperado:" + "\n" + imagenRecuperada);
            
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    
    @Test
    @Sql("classpath:dataset.sql")
    public void renombrar(){

        Imagen imagenAntigua;

        String fileIdSeleccionado = "67cca0f3432c47641676174c";

        String nuevoNombre = "Renombre.jpg";

        try {
            imagenAntigua = imagenServicio.obtener(fileIdSeleccionado).orElse(null);

            Assertions.assertNotNull(imagenAntigua, "La imagen antigua no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagenAntigua);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            Imagen renombrado = imagenServicio.renombrar(imagenAntigua, nuevoNombre, imagenAntigua.getPelicula());

            System.out.println("Imagen renombrada: " + renombrado);

        } catch (Exception e) { throw new RuntimeException(e); }
    }


    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        String fileIdSeleccionado = "67cca0f3432c47641676174c";

        Imagen imagen;

        try {
            imagen = imagenServicio.obtener(fileIdSeleccionado).orElse(null);

            Assertions.assertNotNull(imagen, "La imagen antigua no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagen);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            imagenServicio.eliminar(imagen, true);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            imagenServicio.obtener(fileIdSeleccionado);

        } catch (Exception e) {
            // Realizamos una validacion de la prueba para aceptar que la imagen fue eliminada mediante la excepcion del metodo de obtener
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminarDiversos() {

        List<String> fileIds = new ArrayList<>();

        fileIds.add("67cca0f3432c47641676174c");

        List<Imagen> imagenes = new ArrayList<>();

        int posicion = imagenes.size() - 1;

        for (String fileId : fileIds) {

            try {
                imagenes.add(imagenServicio.obtener(fileId).orElse(null));

                Assertions.assertNotNull(imagenes.get(posicion), "La imagen antigua no debe estar vacía");

                System.out.println("\n" + "Registro encontrado:" + "\n" + imagenes.get(posicion));

            } catch (Exception e) { throw new RuntimeException(e); }
        }

        try {
            imagenServicio.eliminarMultiple(imagenes, true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (String fileId : fileIds) {
            try {
                imagenServicio.obtener(fileId);
    
            } catch (Exception e) {
                // Realizamos una validacion de la prueba para aceptar que la imagen fue
                // eliminada mediante la excepcion del metodo de obtener
                Assertions.assertThrows(Exception.class, () -> {
                    throw e;
                });
    
                System.out.println(e.getMessage());
            }
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        String fileIdSeleccionado = "67cca0f3432c47641676174c";

        try {
            Imagen imagen = imagenServicio.obtener(fileIdSeleccionado).orElse(null);

            Assertions.assertNotNull(imagen, "La imagen no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagen);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerOrigen() {

        String fileIdSeleccionado = "67cca0f3432c47641676174c";

        try {
            Result imagen = imagenKitIo.obtenerDatos(fileIdSeleccionado);

            Assertions.assertNotNull(imagen, "La imagen no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagen);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarImagenesPelicula() {

        Pelicula pelicula;

        try {
            pelicula = peliculaServicio.obtener(new PeliculaAtributoValidator(5)).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        List<String> listaIds;

        try {
            listaIds = imagenServicio.listar(pelicula);

            Assertions.assertEquals(3, listaIds.size());

            System.out.println("\n" + "Listado de registros:");

            listaIds.forEach(System.out::println);

        } catch (Exception e) { throw new RuntimeException(e); }

        // NOTE: Esto se puede eliminar es para obtener los datos de las imagenes de la base de datos, lo digo a causa de que hay metodos que solo necesitan el fileId como el test listar versiones imagen o eliminar diversos

        //REVIEW: En el caso de obtener, es necesario que tengamos el elemento ya que apartir de ese podemos hacer modificaciones a la imagen como actualizar, renombrar o otras

        /* List<Imagen> lista = new ArrayList<>();

        for (String fileId : listaIds) {

            try {

                // Variable para obtener la posicion de la lista 
                int posicion = lista.size() - 1;

                lista.add(imagenServicio.obtener(fileId).orElse(null));

                Assertions.assertNotNull(lista.get(posicion), "La imagen antigua no debe estar vacía");

                System.out.println("\n" + "Registro encontrado:" + "\n" + lista.get(posicion));

            } catch (Exception e) { throw new RuntimeException(e); }
        } */
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarImagenesOrigen() {

        String folderPath = "unicine/peliculas/Encanto";

        try {
            ResultList imagenes = imagenKitIo.listarImagenes(folderPath);

            Assertions.assertNotNull(imagenes, "La imagen no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagenes);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarVersionesImagen() {

        String fileIdSeleccionado = "67cca0f3432c47641676174c";

        try {
            List<VersionArchivo> imagen = imagenServicio.listarVersiones(fileIdSeleccionado);

            Assertions.assertNotNull(imagen, "La imagen no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagen);

        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
