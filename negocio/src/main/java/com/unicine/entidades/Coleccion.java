package com.unicine.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@IdClass(ColeccionComposicion.class)
public class Coleccion implements Serializable {

    // SECTION: Atributos

    @Max(value = 5, message = "La puntuación no puede ser mayor a cinco")
    @Positive(message = "La puntuación debe ser un número positivo")
    @Column(nullable = true)
    private Double puntuacion;

    @Column (nullable = true, length = 10)
    @Enumerated(EnumType.STRING)
    private EstadoPropio estadoPeliculaPropio;

    // SECTION: Relaciones

    @Id
    @ManyToOne
    private Cliente cliente;

    @Id
    @ManyToOne
    private Pelicula pelicula;

    // SECTION: Constructor

    @Builder
    public Coleccion(Double puntuacion, EstadoPropio estadoPeliculaPropio, Cliente cliente, Pelicula pelicula) {
        this.puntuacion = puntuacion;
        this.estadoPeliculaPropio = estadoPeliculaPropio;
        this.cliente = cliente;
        this.pelicula = pelicula;
    }
}