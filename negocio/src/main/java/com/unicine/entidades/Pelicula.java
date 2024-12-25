package com.unicine.entidades;

import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
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

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pelicula implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull(message = "El estado no puede estar vacío")
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private EstadoPelicula estado;

    @ElementCollection
    @Fetch(FetchMode.SELECT)
    private List<GeneroPelicula> generos;

    @NotNull(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de cien caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @ElementCollection
    @Column(nullable = true, length = 150)
    private List< @Size(max = 150, message = "Los nombres del reparto no puede tener mas de cientocincuenta caracteres") String> repartos;

    @Lob
    @NotNull(message = "La sinopsis no puede estar vacía")
    @Column(nullable = false, columnDefinition = "text")
    private String sinopsis;

    @ElementCollection
    @Column(nullable = false)
    private Map<String, String> imagenes;

    @Size(max = 200, message = "La url del trailer no puede tener más de doscientos caracteres")
    @Column(nullable = true, length = 200)
    private String urlTrailer;

    @Max(value = 5, message = "La puntuación no puede ser mayor a cinco")
    @Positive(message = "La puntuación debe ser un número positivo")
    @Column(nullable = false)
    private Double puntuacion;

    @Max(value = 30, message = "La restricción de edad no puede ser mayor a treinta")
    @Positive(message = "La restricción de edad debe ser un número positivo")
    @Column(nullable = true)
    private Integer restriccionEdad;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "pelicula", cascade =  CascadeType.ALL)
    private List<Funcion> funciones;

    @ToString.Exclude
    @OneToMany(mappedBy = "pelicula", cascade =  CascadeType.ALL)
    private List<Coleccion> colecccion;
    
    // SECTION: Constructor

    @Builder
    public Pelicula(EstadoPelicula estado, List<GeneroPelicula> generos, Map<String, String> imagenes, String nombre, List<String> repartos, String sinopsis, String urlTrailer, Double puntuacion, Integer restriccionEdad) {
        this.estado = estado;
        this.generos = generos;
        this.imagenes = imagenes;
        this.nombre = nombre;
        this.repartos = repartos;
        this.sinopsis = sinopsis;
        this.urlTrailer = urlTrailer;
        this.puntuacion = puntuacion;
        this.restriccionEdad = restriccionEdad;
    }

    /*
     * Método que retorna la imagen principal de la película
     * @return String url de la imagen principal
     */
    public String getImagenPrincipal(){
        if (!imagenes.isEmpty()){
            String primera = imagenes.keySet().toArray()[0].toString(); // Recorre el mapa de imágenes y obtiene la primera
            return imagenes.get(primera);
        }
        return ""; // En caso de que no haya imágenes
    }
}
