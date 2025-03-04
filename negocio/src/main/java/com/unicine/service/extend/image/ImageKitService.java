package com.unicine.service.extend.image;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unicine.entity.interfaced.Imagenable;
import com.unicine.util.config.ImageKitConfig;
import com.unicine.util.funtional.image.ProcesadorImagen;
import com.unicine.util.funtional.image.RefactorizadorRuta;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.RenameFileRequest;
import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.models.results.ResultFileDelete;
import io.imagekit.sdk.models.results.ResultList;
import jakarta.annotation.PostConstruct;

@Service
public class ImageKitService {

    @Autowired
    private ImageKitConfig imageKitConfig;

    @Autowired
    private ProcesadorImagen procesadorImagen;

    @Autowired
    private RefactorizadorRuta refactorizadorRuta;

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
     * Método para subir una imagen al servidor de imageKit.io
     *
     * @param file   El archivo de imagen a subir.
     * @param folder La carpeta en imageKit.io donde se almacenará la imagen.
     * @return Un mapa con la respuesta de imageKit.io
     */
    public Result subirImagen(File file, String folder, Imagenable propietario, boolean sobrescribir, String nombrePersonalizado) throws IOException {

        // Convertir la imagen a formato webp conservando el setenta por ciento de calidad
        byte[] fileData = procesadorImagen.convertirFormato(file, 0.7f);

        // Obtener el nombre del archivo sin la extensión
        String name;

        if (nombrePersonalizado == null) {
            name = refactorizadorRuta.nombrarArchivo(file.getName(), propietario);
        } 
        else {
            name = refactorizadorRuta.nombrarArchivo(nombrePersonalizado, propietario);
        }

        FileCreateRequest request = new FileCreateRequest(fileData, name);

        request.setFolder("unicine/" + folder);
        request.setUseUniqueFileName(false);

        if (sobrescribir) {
            request.setOverwriteFile(true);
        }
        
        // Realiza la subida
        try {
            return imageKit.upload(request);
            
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
    public Result actualizarImagen(File fileActual, String fileIdAntiguo, String folder, Imagenable propietario) throws IOException {

        Result archivoExitente = obtenerDatos(fileIdAntiguo);

         // Extraer la respuesta en forma de mapa
        Map<String, Object> respuesta = archivoExitente.getResponseMetaData().getMap();

        String nombreAntiguo = respuesta.get("name").toString();

        try {
            subirImagen(fileActual, folder, propietario, true, nombreAntiguo);

            return renombrarImagen(fileIdAntiguo, fileActual.getName(), propietario);

        } catch (Exception e) {

            throw new IOException("Error al actualizar la imagen: " + e.getMessage());
        }

    }

    /**
     * Renombra un archivo en ImageKit
     * 
     * @param pathArchivo Ruta del archivo a renombrar
     * @param nombreNuevo Nuevo nombre para el archivo
     * @return Resultado de la operación
     */
    public Result renombrarImagen(String fileId, String nombreNuevo, Imagenable propietario) throws IOException {

        Result archivoExistente = obtenerDatos(fileId);

        Map<String, Object> respuesta = archivoExistente.getResponseMetaData().getMap();

        String pathArchivo = respuesta.get("filePath").toString();

        String nombreAntiguo = respuesta.get("name").toString();

        String nombre = nombreAntiguo;

        nombre = refactorizadorRuta.nombrarArchivo(nombreNuevo, propietario);

        RenameFileRequest renameRequest = new RenameFileRequest();

        renameRequest.setFilePath(pathArchivo); // Ruta del archivo a renombrar
        renameRequest.setNewFileName(nombre); // Nuevo nombre para el archivo
        renameRequest.setPurgeCache(true); // Limpia la caché

        try {

            if (!nombreAntiguo.equals(nombre)) {
                imageKit.renameFile(renameRequest);
            }

            return obtenerDatos(fileId);

        } catch (Exception e) {

            throw new IOException("Error al renombrar el archivo: " + e.getMessage());  
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
            Result result = imageKit.deleteFile(fileId);

            System.out.println(result);

            return result;

        } catch (Exception e) {

            throw new IOException("Error al eliminar la imagen: " + e.getMessage());
        }
    }

    public ResultFileDelete eliminarImagenes(List<String> fileIds) throws IOException {

        try {
            ResultFileDelete result = imageKit.bulkDeleteFiles(fileIds);

            System.out.println(result);

            return result;

        } catch (Exception e) {

            throw new IOException("Error al eliminar las imágenes: " + e.getMessage());
        }
    }

    /**
     * Método para obtener los datos de una imagen en el servidor de imageKit.io
     * 
     * @param fileId
     * @return Resultado de la consulta de la imagen
     */
    public Result obtenerDatos(String fileId) throws IOException {

        try {
            Result result = imageKit.getFileDetail(fileId);

            return result;

        } catch (Exception e) {

            throw new IOException("Error al obtener los datos de la imagen: " + e.getMessage());
        }
    }

    /**
     * Método para listar las imágenes en una carpeta del servidor de imageKit.io
     * 
     * @param folderPath
     * @return Resultado de la lista de imágenes
     */
    public ResultList  listarImagenes(String folderPath) throws IOException {

        GetFileListRequest getFileListRequest = new GetFileListRequest();

        getFileListRequest.setPath("/" + folderPath);
        getFileListRequest.setFileType("all");

        getFileListRequest.setSort("ASC_CREATED");


        try {
            ResultList  result = imageKit.getFileList(getFileListRequest);

            return result;

        } catch (Exception e) {

            throw new IOException("Error al listar las imagenes: " + e.getMessage());
        }
    }
}
