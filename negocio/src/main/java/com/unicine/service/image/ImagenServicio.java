package com.unicine.service.image;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.unicine.enums.image.TipoPropietarioImagen;
import com.unicine.transfer.dto.request.ImagenRequest;
import com.unicine.transfer.dto.response.ImagenResponse;
import com.unicine.transfer.dto.response.VersionArchivoResponse;

/**
 * Servicio de gestion de imagenes asociadas a entidades del dominio.
 * Recibe DTOs Request y devuelve DTOs Response.
 */
public interface ImagenServicio {

    // *️⃣ Funciones Generales

    ImagenResponse registrar(ImagenRequest request, MultipartFile file) throws Exception;

    ImagenResponse actualizar(ImagenRequest request, MultipartFile file) throws Exception;

    ImagenResponse restaurar(ImagenRequest request, String versionId) throws Exception;

    ImagenResponse renombrar(ImagenRequest request, String nuevoNombre) throws Exception;

    void eliminar(String codigo, boolean confirmacion) throws Exception;

    void eliminarMultiple(List<String> codigos, boolean confirmacion) throws Exception;

    Optional<ImagenResponse> obtener(String codigo) throws Exception;

    List<String> listar(TipoPropietarioImagen tipoPropietario, Integer codigoPropietario) throws Exception;

    List<VersionArchivoResponse> listarVersiones(String codigo) throws Exception;

    List<ImagenResponse> listarPaginado();

    List<ImagenResponse> listarAscendente();

    List<ImagenResponse> listarDescendente();
}
