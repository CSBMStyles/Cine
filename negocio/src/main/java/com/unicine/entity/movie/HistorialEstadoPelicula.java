package com.unicine.entity.movie;

import com.unicine.enums.movie.EstadoPelicula;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Registra el historial de cambios de estado de una película en una ciudad.
 * Cada fila representa una transición entre estados.
 * Consultar con ORDER BY fechaCambio reconstruye la cronología completa.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class HistorialEstadoPelicula implements Serializable {

    // SECTION: Atributos

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer codigo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPelicula estadoAnterior;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPelicula estadoNuevo;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaCambio;

    // !SECTION
    // SECTION: Relaciones

    @NotNull
    @ManyToOne
    private PeliculaDisposicion peliculaDisposicion;

    // !SECTION
    // SECTION: Constructor

    @Builder
    public HistorialEstadoPelicula(EstadoPelicula estadoAnterior, EstadoPelicula estadoNuevo, LocalDateTime fechaCambio, PeliculaDisposicion peliculaDisposicion) {
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaCambio = fechaCambio;
        this.peliculaDisposicion = peliculaDisposicion;
    }
    // !SECTION
}
