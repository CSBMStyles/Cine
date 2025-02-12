package com.unicine.servicio.complementos.image;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.unicine.util.config.ImageKitConfig;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.exceptions.BadRequestException;
import io.imagekit.sdk.exceptions.ForbiddenException;
import io.imagekit.sdk.exceptions.InternalServerException;
import io.imagekit.sdk.exceptions.NotFoundException;
import io.imagekit.sdk.exceptions.TooManyRequestsException;
import io.imagekit.sdk.exceptions.UnauthorizedException;
import io.imagekit.sdk.exceptions.UnknownException;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.utils.Utils;
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
     * Método para subir una imagen al servidor de ImageKit.io
     *
     * @param file   El archivo de imagen a subir.
     * @param folder La carpeta en ImageKit.io donde se almacenará la imagen.
     * @return Un mapa con la respuesta de ImageKit.io
     * @throws UnknownException 
     */
    public Map<String, String> subirImagen(MultipartFile file, String folder) throws IOException, UnknownException {
        try {
            byte[] bytes = file.getBytes();
            
            FileCreateRequest request = new FileCreateRequest(bytes, file.getOriginalFilename());
            request.setFolder("unicine/" + folder);
            request.setUseUniqueFileName(true);

            Result result = imageKit.upload(request);

            if (result.getResponseMetaData().getHttpStatusCode() == 200) {
                Map<String, String> response = new HashMap<>();
                // Uso de los valores retornados por ImageKit.
                response.put("fileId", result.getFileId());
                response.put("url", result.getUrl());
                return response;
            } else {
                throw new IOException("Error al subir imagen. Código: " + result.getResponseMetaData().getHttpStatusCode());
            }
        } catch (InternalServerException | BadRequestException | UnauthorizedException |
                 ForbiddenException | TooManyRequestsException e) {
            throw new IOException("Error ImageKit: " + e.getMessage(), e);
        }
    }

    /**
     * Método para eliminar una imagen en el servidor de ImageKit.io
     *
     * @param fileId El ID del archivo de la imagen a eliminar.
     * @return True si la eliminación fue exitosa, false en caso contrario.
     */
   /*  public boolean eliminarImagen(String fileId) throws IOException, BadRequestException, UnauthorizedException,
            ForbiddenException, NotFoundException, TooManyRequestsException, InternalServerException {
        Result<?> result = imageKit.deleteFile(fileId);
        if (result.isSuccessful()) {
            return true;
        } else {
            System.err.println(result.getError().getMessage());
            throw new IOException("Error al eliminar la imagen de ImageKit.io: " + result.getError().getMessage());
        }
    } */
}
