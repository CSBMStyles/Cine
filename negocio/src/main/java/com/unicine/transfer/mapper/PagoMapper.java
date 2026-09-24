package com.unicine.transfer.mapper;

import com.unicine.entity.payment.Pago;
import com.unicine.transfer.dto.response.OrdenPagoResponse;

import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper entre la entidad {@link Pago} y sus DTOs de transferencia.
 */
@Mapper
public interface PagoMapper {

    OrdenPagoResponse toResponse(Pago pago);

    List<OrdenPagoResponse> toResponseList(List<Pago> pagos);
}
