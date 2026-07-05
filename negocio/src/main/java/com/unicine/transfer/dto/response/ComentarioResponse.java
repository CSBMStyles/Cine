package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.movie.Comentario}.
 *
 * Incluido:
 * - {@code codigo}, {@code texto}, {@code likes}, {@code dislikes}, {@code fecha}.
 * - Cliente y pelicula anidados.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComentarioResponse {

    private Integer codigo;

    private String texto;

    private Integer likes;

    private Integer dislikes;

    private LocalDateTime fecha;

    private ClienteResponse cliente;

    private PeliculaResponse pelicula;
}
