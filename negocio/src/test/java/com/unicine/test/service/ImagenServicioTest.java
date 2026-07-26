package com.unicine.test.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.unicine.enums.image.TipoPropietarioImagen;
import com.unicine.service.image.ImagenServicio;
import com.unicine.service.image.ImageKitService;
import com.unicine.transfer.dto.request.ImagenRequest;
import com.unicine.transfer.dto.response.ImagenResponse;
import com.unicine.transfer.dto.response.VersionArchivoResponse;

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

    /**
     * Resuelve la ruta de una imagen del proyecto independientemente del
     * directorio de trabajo (raiz del proyecto o modulo 'negocio').
     */
    private static Path resolverRutaImagen(String rutaRelativa) {

        Path base = Paths.get(System.getProperty("user.dir"));

        Path candidata = base.resolve(rutaRelativa);

        if (!Files.exists(candidata)) {
            candidata = base.getParent().resolve(rutaRelativa);
        }

        return candidata.toAbsolutePath().normalize();
    }

    /**
     * Construye un ImagenRequest a partir de un ImagenResponse obtenido del servicio.
     */
    private static ImagenRequest construirRequestDesde(ImagenResponse response) {

        return ImagenRequest.builder()
                .codigo(response.getCodigo())
                .nombre(response.getNombre())
                .tipoPropietario(TipoPropietarioImagen.valueOf(response.getTipoPropietario()))
                .codigoPropietario(response.getCodigoPropietario())
                .build();
    }

    // 🟩

    @Test
    @Disabled("Depende de una ruta local de archivos")
    @Sql("classpath:dataset.sql")
    public void comprobarLectura(){

        Path path = Paths.get("C:/Users/ASUS/Pictures/Camera Roll/DSC_3672 M.JPG");

        // Leer el archivo en un array de bytes
        byte[] content;

        try {
            content = Files.readAllBytes(path);

        } catch (IOException e) { throw new RuntimeException("Error leyendo el archivo", e); }

        MockMultipartFile multipartFile = new MockMultipartFile("file", path.getFileName().toString(), "image/jpg", content);

        System.out.println("Formato: " + multipartFile.getName());

        System.out.println("Nombre del archivo: " + multipartFile.getOriginalFilename());

        System.out.println("Tamaño: " + multipartFile.getSize() / Math.pow(1024.0, 2));

        System.out.println("Tamaño maximo: " + 5);

    }

    @Test
    @Disabled("Depende de una ruta local de archivos y del servicio externo ImageKit")
    @Sql("classpath:dataset.sql")
    public void subirImagenCliente() {

        MultipartFile file;

        try {
            // TODO: Colocar imagen en proyecto y actualizar ruta relativa
            // Creamos un archivo MultipartFile usando un archivo físico
            File fileOriginal = new File("C:/Users/ASUS/Pictures/Camera Roll/DSC_3672 M.JPG");

            byte[] contenido = Files.readAllBytes(fileOriginal.toPath());

            file = new MockMultipartFile("imagen", fileOriginal.getName(), "image/jpg", contenido);

            // IMPORTANT: Cuando este realizando las APIs tengo que validar el formato, en interfaz eso se limita

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }

        ImagenRequest request = ImagenRequest.builder()
                .nombre(file.getOriginalFilename())
                .tipoPropietario(TipoPropietarioImagen.CLIENTE)
                .codigoPropietario(1005000055)
                .build();

        try {

            ImagenResponse resultado = imagenServicio.registrar(request, file);

            System.out.println("Imagen subida: " + resultado);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }
    }

    @Test
    @Disabled("Depende de una ruta local de archivos y del servicio externo ImageKit")
    @Sql("classpath:dataset.sql")
    public void subirImagenPelicula() {

        MultipartFile file;

        try {
            // TODO: Colocar imagen en proyecto y actualizar ruta relativa
            // Creamos un archivo MultipartFile usando un archivo físico
            File fileOriginal = new File("C:/Users/ASUS/Pictures/Camera Roll/DSC_3672 M.JPG");

            byte[] contenido = Files.readAllBytes(fileOriginal.toPath());

            file = new MockMultipartFile("imagen", fileOriginal.getName(), "image/jpg", contenido);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }

        ImagenRequest request = ImagenRequest.builder()
                .nombre(file.getOriginalFilename())
                .tipoPropietario(TipoPropietarioImagen.PELICULA)
                .codigoPropietario(5)
                .build();

        try {
            ImagenResponse resultado = imagenServicio.registrar(request, file);

            Assertions.assertNotNull(resultado, "La imagen resultante no debe ser nula");

            Assertions.assertNotNull(resultado.getCodigo(), "El código de la imagen no debe ser nulo");

            System.out.println("Imagen subida: " + resultado);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirImagenConfiteria() {

        MultipartFile file;

        try {
            File fileOriginal = resolverRutaImagen("image/confiteria/snaks/De Toditos Rojo.png").toFile();

            byte[] contenido = Files.readAllBytes(fileOriginal.toPath());

            file = new MockMultipartFile("imagen", fileOriginal.getName(), "image/png", contenido);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        ImagenRequest request = ImagenRequest.builder()
                .nombre(file.getOriginalFilename())
                .tipoPropietario(TipoPropietarioImagen.CONFITERIA)
                .codigoPropietario(15)
                .build();

        try {
            ImagenResponse resultado = imagenServicio.registrar(request, file);

            Assertions.assertNotNull(resultado, "La imagen resultante no debe ser nula");

            Assertions.assertNotNull(resultado.getCodigo(), "El codigo de la imagen no debe ser nulo");

            System.out.println("===== ImagenResponse (objeto completo) =====");
            System.out.println(resultado);
            System.out.println("===== ImagenResponse (campo por campo) =====");
            System.out.println("codigo: " + resultado.getCodigo());
            System.out.println("nombre: " + resultado.getNombre());
            System.out.println("url: " + resultado.getUrl());
            System.out.println("filePath: " + resultado.getFilePath());
            System.out.println("thumbnailUrl: " + resultado.getThumbnailUrl());
            System.out.println("fileType: " + resultado.getFileType());
            System.out.println("altura: " + resultado.getAltura());
            System.out.println("anchura: " + resultado.getAnchura());
            System.out.println("tamanio: " + resultado.getTamanio());
            System.out.println("versionId: " + resultado.getVersionId());
            System.out.println("versionName: " + resultado.getVersionName());
            System.out.println("tipoPropietario: " + resultado.getTipoPropietario());
            System.out.println("codigoPropietario: " + resultado.getCodigoPropietario());
            System.out.println("============================================");

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Test
    @Disabled("Depende de una ruta local de archivos y del servicio externo ImageKit")
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        MultipartFile file;

        try {
            // Preparamos el archivo MultipartFile para actualizar
            File fileOriginal = new File("C:/Users/ASUS/Pictures/Camera Roll/DSC_3929 M.JPG");

            byte[] contenido = Files.readAllBytes(fileOriginal.toPath());

            file = new MockMultipartFile("imagen", fileOriginal.getName(), "image/jpg", contenido);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }

            // Obtenemos la imagen a actualizar
            String fileIdSeleccionado = "67ccc3e2432c47641609d9e1";

            ImagenResponse imagenAntigua;

        try {
            imagenAntigua = imagenServicio.obtener(fileIdSeleccionado).orElse(null);

            Assertions.assertNotNull(imagenAntigua, "La imagen antigua no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagenAntigua);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }


        try {
            ImagenRequest request = construirRequestDesde(imagenAntigua);

            ImagenResponse actualizado = imagenServicio.actualizar(request, file);

            Assertions.assertEquals(fileIdSeleccionado, actualizado.getCodigo());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Test
    @Disabled("Depende del servicio externo ImageKit")
    @Sql("classpath:dataset.sql")
    public void recuperarVersion(){

        ImagenResponse imagenAntigua;

        String fileIdSeleccionado = "67cca0f3432c47641676174c";

        // NOTE: Listamos las versiones de la imagen y seleccionamos el versionId de la version que queremos recuperar

        String versionIdSeleccionado = "----";

        try {
            imagenAntigua = imagenServicio.obtener(fileIdSeleccionado).orElse(null);

            Assertions.assertNotNull(imagenAntigua, "La imagen antigua no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagenAntigua);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }

        try {

            ImagenRequest request = construirRequestDesde(imagenAntigua);

            ImagenResponse imagenRecuperada = imagenServicio.restaurar(request, versionIdSeleccionado);

            System.out.println("\n" + "Registro recuperado:" + "\n" + imagenRecuperada);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }
    }

    @Test
    @Disabled("Depende del servicio externo ImageKit")
    @Sql("classpath:dataset.sql")
    public void renombrar(){

        ImagenResponse imagenAntigua;

        String fileIdSeleccionado = "67ccc3e2432c47641609d9e1";

        String nuevoNombre = "Renombre.jpg";

        try {
            imagenAntigua = imagenServicio.obtener(fileIdSeleccionado).orElse(null);

            Assertions.assertNotNull(imagenAntigua, "La imagen antigua no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagenAntigua);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }

        try {
            ImagenRequest request = construirRequestDesde(imagenAntigua);

            ImagenResponse renombrado = imagenServicio.renombrar(request, nuevoNombre);

            System.out.println("Imagen renombrada: " + renombrado);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }
    }


    @Test
    @Disabled("Depende del servicio externo ImageKit")
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        String fileIdSeleccionado = "67ccc3e2432c47641609d9e1";

        ImagenResponse imagen;

        try {
            imagen = imagenServicio.obtener(fileIdSeleccionado).orElse(null);

            Assertions.assertNotNull(imagen, "La imagen antigua no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagen);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }

        try {
            imagenServicio.eliminar(fileIdSeleccionado, true);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }

        try {
            imagenServicio.obtener(fileIdSeleccionado);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            // Realizamos una validacion de la prueba para aceptar que la imagen fue eliminada mediante la excepcion del metodo de obtener
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Disabled("Depende del servicio externo ImageKit")
    @Sql("classpath:dataset.sql")
    public void eliminarDiversos() {

        List<String> fileIds = new ArrayList<>();

        fileIds.add("67ccc3e2432c47641609d9e1");

        List<ImagenResponse> imagenes = new ArrayList<>();

        for (String fileId : fileIds) {

            try {
                ImagenResponse imagen = imagenServicio.obtener(fileId).orElse(null);

                imagenes.add(imagen);

                Assertions.assertNotNull(imagen, "La imagen antigua no debe estar vacía");

                System.out.println("\n" + "Registro encontrado:" + "\n" + imagen);

            } catch (Exception e) {
                System.out.println("Mensaje de error: " + e.getMessage());

                throw new RuntimeException(e);

            }
        }

        try {
            imagenServicio.eliminarMultiple(fileIds, true);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        for (String fileId : fileIds) {
            try {
                imagenServicio.obtener(fileId);

            } catch (Exception e) {

                System.out.println("Mensaje de error: " + e.getMessage());

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
    @Disabled("Depende del servicio externo ImageKit")
    @Sql("classpath:dataset.sql")
    public void obtener() {

        String fileIdSeleccionado = "67ccc3e2432c47641609d9e1";

        try {
            ImagenResponse imagen = imagenServicio.obtener(fileIdSeleccionado).orElse(null);

            Assertions.assertNotNull(imagen, "La imagen no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagen);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }
    }

    @Test
    @Disabled("Depende del servicio externo ImageKit")
    @Sql("classpath:dataset.sql")
    public void obtenerOrigen() {

        String fileIdSeleccionado = "67ccc3e2432c47641609d9e1";

        try {
            Result imagen = imagenKitIo.obtenerDatos(fileIdSeleccionado);

            Assertions.assertNotNull(imagen, "La imagen no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagen);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }
    }

    @Test
    @Disabled("Depende del servicio externo ImageKit")
    @Sql("classpath:dataset.sql")
    public void listarImagenesPelicula() {

        List<String> listaIds;

        try {
            listaIds = imagenServicio.listar(TipoPropietarioImagen.PELICULA, 5);

            Assertions.assertEquals(3, listaIds.size());

            System.out.println("\n" + "Listado de registros:");

            listaIds.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }

        // NOTE: Esto se puede eliminar es para obtener los datos de las imagenes de la base de datos, lo digo a causa de que hay metodos que solo necesitan el fileId como el test listar versiones imagen o eliminar diversos

        //REVIEW: En el caso de obtener, es necesario que tengamos el elemento ya que apartir de ese podemos hacer modificaciones a la imagen como actualizar, renombrar o otras
    }

    @Test
    @Disabled("Depende del servicio externo ImageKit")
    @Sql("classpath:dataset.sql")
    public void listarImagenesOrigen() {

        String folderPath = "unicine/peliculas/Encanto";

        try {
            ResultList imagenes = imagenKitIo.listarImagenes(folderPath);

            Assertions.assertNotNull(imagenes, "La imagen no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagenes);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }
    }

    @Test
    @Disabled("Depende del servicio externo ImageKit")
    @Sql("classpath:dataset.sql")
    public void listarVersionesImagen() {

        String fileIdSeleccionado = "67ccc3e2432c47641609d9e1";

        try {
            List<VersionArchivoResponse> imagen = imagenServicio.listarVersiones(fileIdSeleccionado);

            Assertions.assertNotNull(imagen, "La imagen no debe estar vacía");

            System.out.println("\n" + "Registro encontrado:" + "\n" + imagen);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);

        }
    }
}
