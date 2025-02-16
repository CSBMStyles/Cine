package com.unicine.service.extend.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unicine.util.config.ImageKitConfig;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.RenameFileRequest;
import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.models.results.ResultRenameFile;
import jakarta.annotation.PostConstruct;

@Service
public class ImageKitService {

    @Autowired
    private ImageKitConfig imageKitConfig;

    private ImageKit imageKit;

    @PostConstruct
    public void init() {
        // Asignamos al campo miembro en lugar de declarar una variable local.
        this.imageKit = ImageKit.getInstance();
        Configuration config = new Configuration(
                imageKitConfig.getPublicKey(),
                imageKitConfig.getPrivateKey(),
                imageKitConfig.getUrlEndpoint()
        );
        imageKit.setConfig(config);
    }

    /**
     * Método auxiliar para borrar la extensión de un nombre de archivo.
     *
     * @param fileName Nombre original del archivo.
     * @return Nombre del archivo sin la extensión.
     */
    private String borrarExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return index > 0 ? fileName.substring(0, index) : fileName;
    }

    /**
     * Metodo para convertir una imagen a formato webp
     * 
     * @param file Archivo de imagen a convertir
     * @param quality Calidad de la imagen
     * @return
     */
    private byte[] convertirWebp(File file, float quality) throws IOException {

        // Se lee la imagen original
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new RuntimeException("No se pudo leer la imagen: " + file.getAbsolutePath());
        } // Eliminar este bloque en caso que no se identifique la imagen el Buffered es el que lanza la Exception
        
        // Se prepara un stream para almacenar la imagen convertida
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Se obtiene un escritor compatible con el formato webp
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (!writers.hasNext()) {
            throw new RuntimeException("No se encontró un writer para el formato webp");
        }
        
        // Se selecciona el primer escritor disponible
        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            // Se establece el stream de salida para el escritor
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            
            // Si el escritor soporta compresión, se establece la calidad
            if (param.canWriteCompressed()) {
                // Se establece el modo de compresión a "explícito", lo que indica que se especificará manualmente el nivel o el tipo de compresión en lugar de usar un valor predeterminado.
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

                // Se obtiene una lista de los tipos de compresión posibles soportados. Estos generalmente son strings que representan algoritmos o niveles de compresión, y pueden variar dependiendo del formato de imagen.
                String[] compressionTypes = param.getCompressionTypes();

                // Se comprueba que la lista no sea nula y que contenga al menos un tipo de compresión.
                if (compressionTypes != null && compressionTypes.length > 0) {
                    // Se establece el primer tipo de compresión de la lista como el que se usará para la escritura de la imagen. Esto se asume como una opción predeterminada o de mayor prioridad.
                    param.setCompressionType(compressionTypes[0]);
                }
            }

            // Se escribe la imagen en formato webp
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            // Se libera el escritor
            writer.dispose();
        }
        // Se retorna la imagen convertida como un arreglo de bytes
        return baos.toByteArray();
    }


    /**
     * Método para obtener los datos de una imagen en el servidor de imageKit.io
     * 
     * @param fileId
     * @return Resultado de la consulta de la imagen
     */
    public Result obtenerDatosImagen(String fileId) throws IOException {

        try {
            Result result = ImageKit.getInstance().getFileDetail(fileId);
            return result;

        } catch (Exception e) {

            throw new IOException("Error al obtener los datos de la imagen: " + e.getMessage());
        }
    }

    /**
     * Método para subir una imagen al servidor de imageKit.io
     *
     * @param file   El archivo de imagen a subir.
     * @param folder La carpeta en imageKit.io donde se almacenará la imagen.
     * @return Un mapa con la respuesta de imageKit.io
     */
    public Result subirImagen(File file, String folder) throws IOException {

        // Convertir la imagen a formato webp conservando el setenta por ciento de calidad
        byte[] fileData = convertirWebp(file, 0.7f);
        
        FileCreateRequest request = new FileCreateRequest(fileData, borrarExtension(file.getName()));

        request.setFolder("unicine/" + folder);
        request.setUseUniqueFileName(false);
        
        // Realiza la subida
        try {
            Result result = ImageKit.getInstance().upload(request);
            System.out.println(result);
            return result;
            
        } catch (Exception e) {

            throw new IOException("Error al subir la imagen: " + e.getMessage());
        }
    }

    /**
     * Metodo para actualizar una imagen en el servidor de imageKit.io
     * 
     * @param file
     * @param folder
     * @return resultado de la actualización de la imagen
     */
    public ResultRenameFile actualizarImagen(File fileActual, String fileIdAntiguo, String folder) throws IOException {

        Result datosAntiguo = obtenerDatosImagen(fileIdAntiguo);

         // Extraer la respuesta en forma de mapa
        Map<String, Object> respuesta = datosAntiguo.getResponseMetaData().getMap();

        String pathAntiguo = respuesta.get("filePath").toString();

        String nameAntiguo = respuesta.get("name").toString();

        // Convertir la imagen a formato WebP con 70% de calidad
        byte[] fileData = convertirWebp(fileActual, 0.7f);
        
        // Actualizar el nombre de archivo a extensión webp.
        String nameNuevo = borrarExtension(fileActual.getName());

        FileCreateRequest request = new FileCreateRequest(fileData, nameAntiguo); //

        request.setFolder("unicine/" + folder);
        request.setUseUniqueFileName(false); // Para usar el mismo nombre
        request.setOverwriteFile(true); // Sobrescribe el archivo existente

        RenameFileRequest renameRequest = new RenameFileRequest();
        renameRequest.setFilePath(pathAntiguo);
        renameRequest.setNewFileName(nameNuevo); // "Nobre el archivo a renombrar"
        renameRequest.setPurgeCache(true);

        try {
                   ImageKit.getInstance().upload(request);

            return ImageKit.getInstance().renameFile(renameRequest);

        } catch (Exception e) {

            throw new IOException("Error al actualizar la imagen: " + e.getMessage());
        }

    }

    /**
     * Metodo para eliminar una imagen en el servidor de imageKit.io
     * 
     * @param fileId
     * @return resultado de la eliminación de la imagen
     */
    public Result eliminarImagen(String fileId) throws IOException {

        try {
            Result result = ImageKit.getInstance().deleteFile(fileId);
            System.out.println(result);
            return result;

        } catch (Exception e) {

            throw new IOException("Error al eliminar la imagen: " + e.getMessage());
        }
    }
}
