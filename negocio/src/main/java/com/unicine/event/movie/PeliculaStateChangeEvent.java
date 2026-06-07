package com.unicine.event.movie;

import com.unicine.enums.movie.EstadoPelicula;

import java.time.LocalDateTime;

/**
 * Evento emitido cuando una película cambia de estado en una ciudad.
 * Escuchado por notificadores para enviar emails a suscriptores.
 */
public record PeliculaStateChangeEvent(
    Integer peliculaDisposicionId,
    Integer peliculaId,
    Integer ciudadId,
    EstadoPelicula estadoAnterior,
    EstadoPelicula estadoNuevo,
    LocalDateTime fechaCambio
) {}
