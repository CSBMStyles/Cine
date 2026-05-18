package com.unicine.transfer.data;

import com.unicine.entity.showing.Horario;
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.theater.Sala;

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
