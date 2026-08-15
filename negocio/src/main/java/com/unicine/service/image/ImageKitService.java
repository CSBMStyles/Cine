package com.unicine.service.image;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.unicine.entity.image.interfaced.Imagenable;
import com.unicine.enums.image.TipoImagen;
import com.unicine.exception.ExternalServiceException;
import com.unicine.util.config.ImageKitConfig;
import com.unicine.util.funtional.image.ProcesadorImagen;
import com.unicine.util.funtional.image.RefactorizadorRuta;
import com.unicine.util.validation.catalog.domain.ImageErrorCatalog;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageKitService {

    private final ImageKitConfig imageKitConfig;
    private final ProcesadorImagen procesadorImagen;
    private final RefactorizadorRuta refactorizadorRuta;
    private ImageKit imageKit;

    public ImageKitService(ImageKitConfig imageKitConfig, ProcesadorImagen procesadorImagen, RefactorizadorRuta refactorizadorRuta) {
        this.imageKitConfig = imageKitConfig;
        this.procesadorImagen = procesadorImagen;
        this.refactorizadorRuta = refactorizadorRuta;
    }

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
    public Result subirImagen(MultipartFile file, String folder, Imagenable propietario, boolean sobrescribir, String nombrePersonalizado) {
        return subirImagen(file, folder, propietario, sobrescribir, nombrePersonalizado, null, false);
    }

    /**
     * Método para subir una imagen al servidor de imageKit.io usando MultipartFile,
     * permitiendo especificar el tipo de imagen y su perfil de procesamiento.
     * 
     * @param file Archivo MultipartFile a subir
     * @param folder Carpeta donde se guardará la imagen
     * @param propietario Propietario de la imagen
     * @param sobrescribir Si se debe sobrescribir la imagen
     * @param nombrePersonalizado Nombre final para la imagen
     * @param tipoImagen Tipo funcional de imagen; puede ser null para usar el perfil del propietario
     * @param nombreEstable Si el nombre no debe ser generado nuevamente por ImageKit
     * @return Resultado de la subida
     */
    public Result subirImagen(MultipartFile file, String folder, Imagenable propietario, boolean sobrescribir,
                              String nombrePersonalizado, TipoImagen tipoImagen, boolean nombreEstable) {
        // Procesar la imagen según el tipo de propietario y, para películas, su tipo.
        byte[] fileData;
        try {
            fileData = tipoImagen == null
                    ? procesadorImagen.procesar(file, propietario)
                    : procesadorImagen.procesar(file, propietario, tipoImagen);
        } catch (Exception e) {
            // El procesamiento local de la imagen se considera parte del flujo de subida
            // y se mapea al mismo codigo que un fallo del SDK remoto.
            throw new ExternalServiceException(ImageErrorCatalog.DOMAIN_IMAGE_EXTERNAL_UPLOAD_ERROR, e, e.getMessage());
        }

        String name = nombrePersonalizado == null
                ? refactorizadorRuta.nombrarArchivo(file.getOriginalFilename(), propietario)
                : nombrePersonalizado;

        if (!nombreEstable && !sobrescribir) {
            name = refactorizadorRuta.nombrarArchivo(name, propietario);
        }

        FileCreateRequest request = new FileCreateRequest(fileData, name);

        request.setFolder("unicine/" + folder);
        request.setUseUniqueFileName(!nombreEstable);
        request.setOverwriteFile(nombreEstable || sobrescribir);
        
        // Realiza la subida
        try {
            return imageKit.upload(request);

        } catch (Exception e) {
            throw new ExternalServiceException(ImageErrorCatalog.DOMAIN_IMAGE_EXTERNAL_UPLOAD_ERROR, e, e.getMessage());
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
    public Result actualizarImagen(MultipartFile fileActual, String fileIdAntiguo, String folder,
                                   Imagenable propietario, TipoImagen tipoImagen) {
        Result archivoExitente = obtenerDatos(fileIdAntiguo);

        String nombreAntiguo = archivoExitente.getName();

        log.info("Actualizando imagen: nombre actual '{}', fileId '{}'", nombreAntiguo, fileIdAntiguo);

        try {
            return subirImagen(fileActual, folder, propietario, true, nombreAntiguo, tipoImagen, true);

        } catch (Exception e) {

            throw new ExternalServiceException(ImageErrorCatalog.DOMAIN_IMAGE_EXTERNAL_UPDATE_ERROR, e, e.getMessage());
        }
    }

    /**
     * Método para restaurar una versión de una imagen en el servidor de ImageKit
     * 
     * @param fileId Identificador del arhivo
     * @param versionId Identificador de la versión
     * @return Resultado de la restauración
     */
    public Result restaurarVersion(String fileId, String versionId) {

        try {
            return imageKit.restoreFileVersion(fileId, versionId);

        } catch (Exception e) {

            throw new ExternalServiceException(ImageErrorCatalog.DOMAIN_IMAGE_EXTERNAL_RESTORE_VERSION_ERROR, e, e.getMessage());
        }
    }

    /**
     * Renombra un archivo en ImageKit
     * 
     * @param pathArchivo Ruta del archivo a renombrar
     * @param nombreNuevo Nuevo nombre para el archivo
     * @return Resultado de la operación
     */
    public Result renombrarImagen(String fileId, String nombreNuevo, Imagenable propietario) {

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

            throw new ExternalServiceException(ImageErrorCatalog.DOMAIN_IMAGE_EXTERNAL_RENAME_FILE_ERROR, e, e.getMessage());
        }
    }

    /**
     * Metodo para eliminar una imagen en el servidor de imageKit.io
     * 
     * @param fileId
     * @return Resultado de la eliminación de la imagen
     */
    public Result eliminarImagen(String fileId) {

        try {
            Result result = imageKit.deleteFile(fileId);

            return result;

        } catch (Exception e) {

            throw new ExternalServiceException(ImageErrorCatalog.DOMAIN_IMAGE_EXTERNAL_DELETE_IMAGE_ERROR, e, e.getMessage());
        }
    }

    public ResultFileDelete eliminarImagenes(List<String> fileIds) {

        try {
            ResultFileDelete result = imageKit.bulkDeleteFiles(fileIds);

            return result;

        } catch (Exception e) {

            throw new ExternalServiceException(ImageErrorCatalog.DOMAIN_IMAGE_EXTERNAL_DELETE_IMAGES_ERROR, e, e.getMessage());
        }
    }

    /**
     * Método para obtener los datos de una imagen en el servidor de imageKit.io
     * 
     * @param fileId
     * @return Resultado de la consulta de la imagen
     */
    public Result obtenerDatos(String fileId) {

        try {
            Result result = imageKit.getFileDetail(fileId);

            return result;

        } catch (Exception e) {

            throw new ExternalServiceException(ImageErrorCatalog.DOMAIN_IMAGE_EXTERNAL_GET_IMAGE_DATA_ERROR, e, e.getMessage());
        }
    }

    /**
     * Método para listar las imágenes en una carpeta del servidor de imageKit.io
     * 
     * @param folderPath
     * @return Resultado de la lista de imágenes
     */
    public ResultList listarImagenes(String folderPath) {

        GetFileListRequest getFileListRequest = new GetFileListRequest();

        getFileListRequest.setPath("/unicine/" + folderPath);
        getFileListRequest.setFileType("all");

        getFileListRequest.setSort("ASC_CREATED");

        try {
            ResultList result = imageKit.getFileList(getFileListRequest);

            return result;

        } catch (Exception e) {

            throw new ExternalServiceException(ImageErrorCatalog.DOMAIN_IMAGE_EXTERNAL_LIST_IMAGES_ERROR, e, e.getMessage());
        }
    }

    /**
     * Metodo para listar las versiones que tiene una imagen
     * 
     * @param fileId Identificador del arvhivo
     * @return Resultado de la version
     */
    public ResultFileVersions listarVersiones(String fileId) {

        try {
            ResultFileVersions result = imageKit.getFileVersions(fileId);

            return result;

        } catch (Exception e) {

            throw new ExternalServiceException(ImageErrorCatalog.DOMAIN_IMAGE_EXTERNAL_LIST_IMAGE_VERSIONS_ERROR, e, e.getMessage());
        }
    }
}
