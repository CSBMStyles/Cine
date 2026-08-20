package com.unicine.entity.movie;

import com.unicine.entity.theater.Ciudad;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.unicine.entity.movie.composed.PeliculaDisposicionCompuesta;
import com.unicine.enums.movie.EstadoPelicula;
import com.unicine.util.validation.catalog.ValidationMessages;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@IdClass(PeliculaDisposicionCompuesta.class)
public class PeliculaDisposicion implements Serializable {

    // SECTION: Atributos

    @NotNull(message = ValidationMessages.FIELD_REQUIRED)
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EstadoPelicula estadoPelicula;

    @Column(nullable = true)
    private LocalDateTime fechaFuncionInicial;

    // !SECTION
    // SECTION: Relaciones

    @Id
    @ManyToOne
    @NotNull(message = ValidationMessages.SHOWING_MOVIE_NOT_NULL)
    private Pelicula pelicula;

    @Id
    @ManyToOne
    @NotNull(message = ValidationMessages.THEATER_CITY_NOT_NULL)
    private Ciudad ciudad;
    
    // !SECTION
    // SECTION: Constructor

    @Builder
    public PeliculaDisposicion(Pelicula pelicula, Ciudad ciudad) {
        this.pelicula = pelicula;
        this.ciudad = ciudad;
    }
    // !SECTION
}
