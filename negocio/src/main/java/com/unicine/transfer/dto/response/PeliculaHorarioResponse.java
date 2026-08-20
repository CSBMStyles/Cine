package com.unicine.transfer.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida con el detalle de horarios y salas de una pelicula en un teatro.
 *
 * Reemplaza al DTO legacy {@code DetallePeliculaHorarioDTO} que exponia
 * entidades JPA directamente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PeliculaHorarioResponse {

    private Integer codigoPelicula;

    private String nombrePelicula;

    private Integer codigoHorario;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private Integer codigoSala;

    private String nombreSala;
}
