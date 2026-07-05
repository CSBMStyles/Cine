package com.unicine.transfer.mapper;

import com.unicine.entity.purchase.CuponCliente;
import com.unicine.transfer.dto.request.CuponClienteRequest;
import com.unicine.transfer.dto.response.CuponClienteResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link CuponCliente} y sus DTOs de transferencia.
 */
@Mapper(uses = {CuponMapper.class, ClienteMapper.class})
public interface CuponClienteMapper {

    CuponClienteResponse toResponse(CuponCliente cuponCliente);

    List<CuponClienteResponse> toResponseList(List<CuponCliente> cuponClientes);

    @Mapping(target = "cupon.codigo", source = "cuponCodigo")
    @Mapping(target = "cliente.cedula", source = "clienteCedula")
    @Mapping(target = "compra", ignore = true)
    CuponCliente toEntity(CuponClienteRequest request);
}
