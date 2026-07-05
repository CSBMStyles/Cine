package com.unicine.transfer.mapper;

import com.unicine.entity.confiteria.HistorialPrecioPresentacion;
import com.unicine.transfer.dto.request.HistorialPrecioPresentacionRequest;
import com.unicine.transfer.dto.response.HistorialPrecioPresentacionResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link HistorialPrecioPresentacion} y sus DTOs de transferencia.
 */
@Mapper(uses = ConfiteriaPresentacionMapper.class)
public interface HistorialPrecioPresentacionMapper {

    HistorialPrecioPresentacionResponse toResponse(HistorialPrecioPresentacion historial);

    List<HistorialPrecioPresentacionResponse> toResponseList(List<HistorialPrecioPresentacion> historiales);

    @Mapping(target = "presentacion.codigo", source = "presentacionCodigo")
    HistorialPrecioPresentacion toEntity(HistorialPrecioPresentacionRequest request);
}
