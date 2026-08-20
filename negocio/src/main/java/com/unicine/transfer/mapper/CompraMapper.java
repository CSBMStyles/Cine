package com.unicine.transfer.mapper;

import com.unicine.entity.purchase.Compra;
import com.unicine.transfer.dto.request.CompraRequest;
import com.unicine.transfer.dto.response.CompraResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Compra} y sus DTOs de transferencia.
 */
@Mapper(uses = {ClienteMapper.class, FuncionMapper.class, CuponClienteMapper.class, EntradaMapper.class, CompraConfiteriaMapper.class})
public interface CompraMapper {

    CompraResponse toResponse(Compra compra);

    List<CompraResponse> toResponseList(List<Compra> compras);

    @Mapping(target = "cuponCliente.codigo", source = "cuponClienteCodigo")
    @Mapping(target = "cliente.cedula", source = "clienteCedula")
    @Mapping(target = "funcion.codigo", source = "funcionCodigo")
    Compra toEntity(CompraRequest request);
}
