package com.unicine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.unicine.entidades.Horario;
import com.unicine.entidades.Pelicula;
import com.unicine.entidades.Sala;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DetallePeliculaHorarioDTO {

    private Pelicula pelicula;

    private Horario horario;

    private Sala sala;
}
