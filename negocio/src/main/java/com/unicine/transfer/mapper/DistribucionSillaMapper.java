package com.unicine.transfer.mapper;

import com.unicine.entity.theater.DistribucionSilla;
import com.unicine.transfer.dto.request.DistribucionSillaRequest;
import com.unicine.transfer.dto.response.DistribucionSillaResponse;

import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper entre la entidad {@link DistribucionSilla} y sus DTOs de transferencia.
 */
@Mapper
public interface DistribucionSillaMapper {

    DistribucionSillaResponse toResponse(DistribucionSilla distribucionSilla);

    List<DistribucionSillaResponse> toResponseList(List<DistribucionSilla> distribuciones);

    DistribucionSilla toEntity(DistribucionSillaRequest request);
}
