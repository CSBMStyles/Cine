package com.unicine.transfer.mapper;

import com.unicine.entity.purchase.Cupon;
import com.unicine.transfer.dto.request.CuponRequest;
import com.unicine.transfer.dto.response.CuponResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Cupon} y sus DTOs de transferencia.
 */
@Mapper
public interface CuponMapper {

    CuponResponse toResponse(Cupon cupon);

    List<CuponResponse> toResponseList(List<Cupon> cupones);

    @Mapping(target = "cuponClientes", ignore = true)
    Cupon toEntity(CuponRequest request);
}
