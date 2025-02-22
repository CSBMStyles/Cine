package com.unicine.transfer.mapper;

import org.springframework.stereotype.Component;

import com.unicine.transfer.data.DetalleFuncionesDTO;
import com.unicine.transfer.projetion.DetalleFuncionesProjection;

@Component
public class DetalleFuncionMapper {

    public DetalleFuncionesDTO convertirDTO(DetalleFuncionesProjection projection) {
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
