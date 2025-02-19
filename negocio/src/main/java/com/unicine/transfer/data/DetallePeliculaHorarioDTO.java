package com.unicine.transfer.data;

import com.unicine.entity.Horario;
import com.unicine.entity.Pelicula;
import com.unicine.entity.Sala;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DetallePeliculaHorarioDTO {

    private Pelicula pelicula;

    private Horario horario;

    private Sala sala;
}
