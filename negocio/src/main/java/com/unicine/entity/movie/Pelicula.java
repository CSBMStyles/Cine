package com.unicine.entity.movie;

import com.unicine.entity.image.Imagen;
import com.unicine.entity.showing.Funcion;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.unicine.entity.image.interfaced.Imagenable;
import com.unicine.enums.movie.EstadoPelicula;
import com.unicine.enums.movie.GeneroPelicula;
import com.unicine.util.validation.catalog.ValidationMessages;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pelicula implements Serializable, Imagenable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull(message = ValidationMessages.FIELD_REQUIRED)
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private EstadoPelicula estado;

    @ElementCollection
    @Fetch(FetchMode.SELECT)
    private List<GeneroPelicula> generos;

    @NotBlank(message = ValidationMessages.MOVIE_NAME_NOT_BLANK)
    @Size(max = 100, message = ValidationMessages.MOVIE_NAME_SIZE_MAX_100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @ElementCollection
    @Column(nullable = true)
    private Map<@Size(max = 150, message = ValidationMessages.MOVIE_ACTOR_ROLE_SIZE_MAX_150) String,
                @Size(max = 150, message = ValidationMessages.MOVIE_ACTOR_NAME_SIZE_MAX_150) String> repartos;

    @Lob
    @NotNull(message = ValidationMessages.MOVIE_SYNOPSIS_NOT_BLANK)
    @Column(nullable = false, columnDefinition = "text")
    private String sinopsis;

    @Size(max = 200, message = ValidationMessages.MOVIE_TRAILER_URL_SIZE_MAX_200)
    @Column(nullable = true, length = 200)
    private String urlTrailer;

    @NotNull(message = ValidationMessages.VALUE_NOT_NULL)
    @Max(value = 5, message = ValidationMessages.MOVIE_RATING_MAX_5)
    @Positive(message = ValidationMessages.MOVIE_RATING_POSITIVE)
    @Column(nullable = false)
    private Double puntuacion;

    @Max(value = 30, message = ValidationMessages.MOVIE_AGE_RESTRICTION_MAX_30)
    @Positive(message = ValidationMessages.MOVIE_AGE_RESTRICTION_POSITIVE)
    @Column(nullable = true)
    private Integer restriccionEdad;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "pelicula", cascade =  CascadeType.ALL)
    private List<Funcion> funciones;

    @ToString.Exclude
    @OneToMany(mappedBy = "pelicula", cascade =  CascadeType.ALL)
    private List<Coleccion> colecccion;

    @ToString.Exclude
    @OneToMany(mappedBy = "pelicula", cascade =  CascadeType.ALL)
    private List<PeliculaDisposicion> peliculaDisposicion;

    @ToString.Exclude
    @OneToMany(mappedBy = "pelicula", cascade =  CascadeType.ALL)
    private List<Imagen> imagenes;
    
    // SECTION: Constructor

    @Builder
    public Pelicula(EstadoPelicula estado, List<GeneroPelicula> generos, String nombre, Map<String, String> repartos, String sinopsis, String urlTrailer, Double puntuacion, Integer restriccionEdad) {
        this.estado = estado;
        this.generos = generos;
        this.nombre = nombre;
        this.repartos = repartos;
        this.sinopsis = sinopsis;
        this.urlTrailer = urlTrailer;
        this.puntuacion = puntuacion;
        this.restriccionEdad = restriccionEdad;
    }

    @Override
    public String getCarpetaPrefijo() {
        return "peliculas";
    }
}
