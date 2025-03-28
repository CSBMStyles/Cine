package com.unicine.service.extend.image;

import java.util.List;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import io.imagekit.sdk.models.results.ResultFileVersions;
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
     * Método para subir una imagen al servidor de imageKit.io usando MultipartFile
     * 
     * @param file Archivo MultipartFile a subir
     * @param folder Carpeta donde se guardará la imagen
     * @param propietario Propietario de la imagen
     * @param sobrescribir Si se debe sobrescribir la imagen
     * @param nombrePersonalizado Nombre personalizado para la imagen
     * @return Resultado de la subida
     */
    public Result subirImagen(MultipartFile file, String folder, Imagenable propietario, boolean sobrescribir, String nombrePersonalizado) throws IOException {
        // Convertir la imagen a formato webp conservando el setenta por ciento de calidad
        byte[] fileData = procesadorImagen.convertirFormato(file, 0.7f);

        // Obtener el nombre del archivo sin la extensión
        String name;

        if (nombrePersonalizado == null) {
            name = refactorizadorRuta.nombrarArchivo(file.getOriginalFilename(), propietario);
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
            throw new IOException("Error al subir la imagen: " + e);
        }
    }

    /**
     * Metodo para actualizar una imagen en el servidor de imageKit.io usando MultipartFile
     * 
     * @param fileActual Archivo MultipartFile actualizado
     * @param fileIdAntiguo ID del archivo a actualizar
     * @param folder Carpeta donde está la imagen
     * @param propietario Propietario de la imagen
     * @return resultado de la actualización de la imagen
     */
    public Result actualizarImagen(MultipartFile fileActual, String fileIdAntiguo, String folder, Imagenable propietario) throws IOException {
        Result archivoExitente = obtenerDatos(fileIdAntiguo);

        String nombreAntiguo = archivoExitente.getName();

        System.out.println("Nombre antiguo: " + nombreAntiguo);

        try {
            subirImagen(fileActual, folder, propietario, true, nombreAntiguo);

            return renombrarImagen(fileIdAntiguo, fileActual.getOriginalFilename(), propietario);

        } catch (Exception e) {

            throw new IOException("Error al actualizar la imagen: " + e);
        }
    }

    /**
     * Método para restaurar una versión de una imagen en el servidor de ImageKit
     * 
     * @param fileId Identificador del arhivo
     * @param versionId Identificador de la versión
     * @return Resultado de la restauración
     */
    public Result restaurarVersion(String fileId, String versionId) throws IOException {

        try {
            return imageKit.restoreFileVersion(fileId, versionId);

        } catch (Exception e) {

            throw new IOException("Error al restaurar la versión de la imagen: " + e);
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

        String pathArchivo = archivoExistente.getFilePath();

        String nombreAntiguo = archivoExistente.getName();

        String nombre = refactorizadorRuta.nombrarArchivo(nombreNuevo, propietario);

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

            throw new IOException("Error al renombrar el archivo: " + e);  
        }
    }

    /**
     * Metodo para eliminar una imagen en el servidor de imageKit.io
     * 
     * @param fileId
     * @return Resultado de la eliminación de la imagen
     */
    public Result eliminarImagen(String fileId) throws IOException {

        try {
            Result result = imageKit.deleteFile(fileId);

            return result;

        } catch (Exception e) {

            throw new IOException("Error al eliminar la imagen: " + e);
        }
    }

    public ResultFileDelete eliminarImagenes(List<String> fileIds) throws IOException {

        try {
            ResultFileDelete result = imageKit.bulkDeleteFiles(fileIds);

            return result;

        } catch (Exception e) {

            throw new IOException("Error al eliminar las imágenes: " + e);
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

            throw new IOException("Error al obtener los datos de la imagen: " + e);
        }
    }

    /**
     * Método para listar las imágenes en una carpeta del servidor de imageKit.io
     * 
     * @param folderPath
     * @return Resultado de la lista de imágenes
     */
    public ResultList listarImagenes(String folderPath) throws IOException {

        GetFileListRequest getFileListRequest = new GetFileListRequest();

        getFileListRequest.setPath("/unicine/" + folderPath);
        getFileListRequest.setFileType("all");

        getFileListRequest.setSort("ASC_CREATED");

        try {
            ResultList result = imageKit.getFileList(getFileListRequest);

            return result;

        } catch (Exception e) {

            throw new IOException("Error al listar las imagenes: " + e);
        }
    }

    /**
     * Metodo para listar las versiones que tiene una imagen
     * 
     * @param fileId Identificador del arvhivo
     * @return Resultado de la version
     */
    public ResultFileVersions listarVersiones(String fileId) throws IOException {

        try {
            ResultFileVersions result = imageKit.getFileVersions(fileId);

            return result;

        } catch (Exception e) {

            throw new IOException("Error al listar las versiones de la imagen: " + e);
        }
    }
}
