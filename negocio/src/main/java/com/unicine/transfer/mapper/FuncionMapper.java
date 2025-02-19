package com.unicine.transfer.mapper;

import com.unicine.transfer.data.DetalleFuncionesDTO;
import com.unicine.transfer.projetion.DetalleFuncionesProjection;

public class FuncionMapper {

    public static DetalleFuncionesDTO convertirDTO(DetalleFuncionesProjection projection) {
        return new DetalleFuncionesDTO(
            projection.getNombrePelicula(),
            projection.getEstadoPelicula(),
            projection.getImagenes(),
            projection.getCodigoSala(),
            projection.getDireccionTeatro(),
            projection.getNombreCiudad(),
            projection.getHorario()
        );
    }
}
