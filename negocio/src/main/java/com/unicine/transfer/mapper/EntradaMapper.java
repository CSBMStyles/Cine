package com.unicine.transfer.mapper;

import com.unicine.entity.purchase.Entrada;
import com.unicine.transfer.dto.request.EntradaRequest;
import com.unicine.transfer.dto.response.EntradaResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Entrada} y sus DTOs de transferencia.
 */
@Mapper(uses = FuncionMapper.class)
public interface EntradaMapper {

    EntradaResponse toResponse(Entrada entrada);

    List<EntradaResponse> toResponseList(List<Entrada> entradas);

    @Mapping(target = "compra.codigo", source = "compraCodigo")
    @Mapping(target = "funcion.codigo", source = "funcionCodigo")
    Entrada toEntity(EntradaRequest request);
}
