package com.unicine.transfer.mapper;

import com.unicine.entity.showing.FuncionEsquema;
import com.unicine.transfer.dto.request.FuncionEsquemaRequest;
import com.unicine.transfer.dto.response.FuncionEsquemaResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link FuncionEsquema} y sus DTOs de transferencia.
 */
@Mapper
public interface FuncionEsquemaMapper {

    @Mapping(target = "funcionCodigo", source = "funcion.codigo")
    FuncionEsquemaResponse toResponse(FuncionEsquema funcionEsquema);

    List<FuncionEsquemaResponse> toResponseList(List<FuncionEsquema> funcionEsquemas);

    @Mapping(target = "funcion.codigo", source = "funcionCodigo")
    FuncionEsquema toEntity(FuncionEsquemaRequest request);
}
