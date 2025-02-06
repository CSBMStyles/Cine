package com.unicine.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


import com.unicine.util.emuns.FormatoPelicula;
import com.unicine.util.emuns.GeneroPelicula;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FuncionInterseccionDTO {

    private String nombreSala;

    private String nombrePelicula;

    private FormatoPelicula formato;

    private Map<String, String> imagenesPelicula;

    private List<GeneroPelicula> generos;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

}
