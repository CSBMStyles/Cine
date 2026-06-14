package com.unicine.entity.movie;

import com.unicine.entity.user.Cliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.io.Serializable;

import com.unicine.entity.movie.composed.ColeccionCompuesta;
import com.unicine.enums.movie.EstadoPropio;
import com.unicine.util.validation.catalog.ValidationMessages;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@IdClass(ColeccionCompuesta.class)
public class Coleccion implements Serializable {

    // SECTION: Atributos

    @Max(value = 5, message = ValidationMessages.MOVIE_RATING_MAX_FIVE)
    @Positive(message = ValidationMessages.MOVIE_RATING_POSITIVE)
    @Column(nullable = true)
    private Double puntuacion;

    @Column (nullable = true, length = 10)
    @Enumerated(EnumType.STRING)
    private EstadoPropio estadoPeliculaPropio;

    @Column(nullable = false)
    private Boolean notificacionActiva = true;

    // SECTION: Relaciones

    @Id
    @ManyToOne
    @NotNull(message = ValidationMessages.CLIENT_COUPON_CLIENT_NOT_NULL)
    private Cliente cliente;

    @Id
    @ManyToOne
    @NotNull(message = ValidationMessages.SHOWING_MOVIE_NOT_NULL)
    private Pelicula pelicula;

    // SECTION: Constructor

    @Builder
    public Coleccion(Double puntuacion, EstadoPropio estadoPeliculaPropio, Boolean notificacionActiva, Cliente cliente, Pelicula pelicula) {
        this.puntuacion = puntuacion;
        this.estadoPeliculaPropio = estadoPeliculaPropio;
        this.notificacionActiva = notificacionActiva != null ? notificacionActiva : true;
        this.cliente = cliente;
        this.pelicula = pelicula;
    }
}