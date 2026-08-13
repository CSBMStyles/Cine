package com.unicine.transfer.mapper;

import com.unicine.entity.image.Imagen;
import com.unicine.transfer.dto.request.ImagenRequest;
import com.unicine.transfer.dto.response.ImagenResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Imagen} y sus DTOs de transferencia.
 */
@Mapper
public interface ImagenMapper {

    @Mapping(target = "nombre", ignore = true)
    @Mapping(target = "filePath", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "fileType", ignore = true)
    @Mapping(target = "altura", ignore = true)
    @Mapping(target = "anchura", ignore = true)
    @Mapping(target = "tamanio", ignore = true)
    @Mapping(target = "versionId", ignore = true)
    @Mapping(target = "versionName", ignore = true)
    @Mapping(target = "tipoPropietario", ignore = true)
    @Mapping(target = "codigoPropietario", ignore = true)
    ImagenResponse toResponse(Imagen imagen);

    List<ImagenResponse> toResponseList(List<Imagen> imagenes);

    @Mapping(target = "codigo", source = "codigo")
    @Mapping(target = "url", ignore = true)
    @Mapping(target = "orden", ignore = true)
    @Mapping(target = "principal", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "administrador", ignore = true)
    @Mapping(target = "administradorTeatro", ignore = true)
    @Mapping(target = "pelicula", ignore = true)
    @Mapping(target = "confiteria", ignore = true)
    Imagen toEntity(ImagenRequest request);
}
