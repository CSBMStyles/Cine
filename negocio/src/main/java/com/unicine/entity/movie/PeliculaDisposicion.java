package com.unicine.entity.movie;

import com.unicine.entity.theater.Ciudad;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
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

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@IdClass(PeliculaDisposicionCompuesta.class)
public class PeliculaDisposicion implements Serializable {

    // SECTION: Atributos

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EstadoPelicula estadoPelicula;

    @Column(nullable = true)
    private LocalDateTime fechaFuncionInicial;

    // SECTION: Relaciones

    @Id
    @ManyToOne
    private Pelicula pelicula;

    @Id
    @ManyToOne
    private Ciudad ciudad;
    
    // SECTION: Constructor

    @Builder
    public PeliculaDisposicion(Pelicula pelicula, Ciudad ciudad) {
        this.pelicula = pelicula;
        this.ciudad = ciudad;
    }
}
