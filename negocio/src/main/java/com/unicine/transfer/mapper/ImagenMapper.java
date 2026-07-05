package com.unicine.transfer.mapper;

import com.unicine.entity.image.Imagen;
import com.unicine.transfer.dto.request.ImagenRequest;
import com.unicine.transfer.dto.response.ImagenResponse;

import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper entre la entidad {@link Imagen} y sus DTOs de transferencia.
 */
@Mapper
public interface ImagenMapper {

    ImagenResponse toResponse(Imagen imagen);

    List<ImagenResponse> toResponseList(List<Imagen> imagenes);

    Imagen toEntity(ImagenRequest request);
}
