package com.unicine.entity;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

import com.unicine.enumeration.FormatoPelicula;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Funcion implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @PositiveOrZero(message = "El precio debe ser un número positivo")
    @Column(nullable = false)
    private Double precio;

    @NotNull(message = "El formato no puede estar vacío")
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private FormatoPelicula formato;

    // SECTION: Relaciones

    @ManyToOne
    @NotNull(message = "La sala no puede estar vacía")
    private Sala sala;

    @OneToOne(cascade = CascadeType.ALL)
    @NotNull(message = "El horario no puede estar vacío")
    private Horario horario;

    @ManyToOne
    @NotNull(message = "La película no puede estar vacía")
    private Pelicula pelicula;

    @ToString.Exclude
    @OneToOne(mappedBy = "funcion", cascade = CascadeType.ALL)
    private FuncionEsquema funcionEsquema;

    @ToString.Exclude
    @OneToMany(mappedBy = "funcion", cascade = CascadeType.ALL)
    private List<Compra> compras;
    
    // SECTION: Constructor

    @Builder
    public Funcion(FormatoPelicula formato, Sala sala, Horario horario, Pelicula pelicula) {
        this.formato = formato;
        this.sala = sala;
        this.horario = horario;
        this.pelicula = pelicula;
    }
}
