package com.unicine.transfer.mapper;

import com.unicine.entity.purchase.CompraConfiteria;
import com.unicine.transfer.dto.request.CompraConfiteriaRequest;
import com.unicine.transfer.dto.response.CompraConfiteriaResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link CompraConfiteria} y sus DTOs de transferencia.
 */
@Mapper(uses = ConfiteriaPresentacionMapper.class)
public interface CompraConfiteriaMapper {

    CompraConfiteriaResponse toResponse(CompraConfiteria compraConfiteria);

    List<CompraConfiteriaResponse> toResponseList(List<CompraConfiteria> compraConfiterias);

    @Mapping(target = "compra.codigo", source = "compraCodigo")
    @Mapping(target = "presentacion.codigo", source = "presentacionCodigo")
    CompraConfiteria toEntity(CompraConfiteriaRequest request);
}
