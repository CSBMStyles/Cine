package com.unicine.transfer.mapper;

import com.unicine.transfer.data.FuncionInterseccionDTO;
import com.unicine.transfer.projetion.FuncionInterseccionProjection;

public class FuncionInterseccionMapper {

    public static FuncionInterseccionDTO convertirDTO(FuncionInterseccionProjection projection) {
        return new FuncionInterseccionDTO(
            projection.getNombreSala(),

            projection.getNombrePelicula(),

            projection.getFormato(),

            projection.getImagen(),

            projection.getGeneros(),

            projection.getFechaInicio(),

            projection.getFechaFin()
        );
    }
}
