package com.unicine.servicio.complementos.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.unicine.util.config.CloudinaryConfig;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    private Cloudinary cloudinary;

    private Map<String, String> config;

    @Autowired
    private CloudinaryConfig secretConfig;

    @PostConstruct
    public void init() {
        config = new HashMap<>();

        config.put("cloud_name", secretConfig.getCloudName());
        config.put("api_key", secretConfig.getApiKey());
        config.put("api_secret", secretConfig.getApiSecret());

        // Inicia instancia de configuracion
        cloudinary = new Cloudinary(config);
    }

    /**
     * Método para subir una imagen en el servidor
     * @param file El archivo de imagen a subir.
     * @param carpeta La carpeta en Cloudinary donde se almacenará la imagen.
     * @return Un mapa con la respuesta
     * @throws Exception Si ocurre un error durante la subida.
     */
    @SuppressWarnings("rawtypes")
    public Map subirImagen(File file, String carpeta) throws Exception {
        
        // Subir la imagen a Cloudinary en la carpeta especificada
        Map respuesta = cloudinary.uploader().upload(file, ObjectUtils.asMap("folder", String.format("unicine/%s", carpeta)));
        return respuesta;
    }

    /**
     * Método para eliminar una imagen en el servidor
     * @param imagen El ID de la imagen a eliminar.
     * @return Un mapa con la respuesta
     * @throws Exception Si ocurre un error durante la eliminación.
     */
    @SuppressWarnings("rawtypes")
    public Map eliminarImagen(String idImagen) throws Exception {
        
        // Eliminar la imagen de Cloudinary usando el ID especificado
        Map respuesta = cloudinary.uploader().destroy(idImagen, ObjectUtils.emptyMap());
        return respuesta;
    }
}