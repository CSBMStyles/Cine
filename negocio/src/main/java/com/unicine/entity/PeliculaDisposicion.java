package com.unicine.entity;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

import com.unicine.enumeration.EstadoPelicula;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PeliculaDisposicion implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull(message = "El estado no puede estar vacío")
    @Column(nullable = false, length = 50)
    private EstadoPelicula estadoPelicula;

    // SECTION: Relaciones

    @ManyToOne
    @NotNull(message = "La función no puede estar vacía")
    private Pelicula pelicula;

    @ManyToOne
    @NotNull(message = "La ciudad no puede estar vacía")
    private Ciudad ciudad;
    
    // SECTION: Constructor

    @Builder
    public PeliculaDisposicion(EstadoPelicula estadoPelicula, Pelicula pelicula, Ciudad ciudad) {
        this.estadoPelicula = estadoPelicula;
        this.pelicula = pelicula;
        this.ciudad = ciudad;
    }
}
