package com.unicine.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.List;

import com.unicine.util.validation.annotation.MultiPattern;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ciudad implements Serializable {
    
    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotBlank(message = "El nombre no puede estar en blanco")
    @MultiPattern({
        @Pattern(regexp = ".{2,}", message = "El nombre de la ciudad no debe ser menor a dos caracteres"),
        @Pattern(regexp = ".{1,100}", message = "El nombre de la ciudad no debe pasar los cien caracteres"),
        @Pattern(regexp = "^[a-zA-Z ]+$", message = "El nombre de la ciudad solo puede contener letras y espacios")
    })
    @Column(nullable = false, length = 100)
    private String nombre;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "ciudad", cascade = CascadeType.ALL)
    private List<Teatro> teatros;

    @ToString.Exclude
    @OneToMany(mappedBy = "ciudad", cascade =  CascadeType.ALL)
    private List<PeliculaDisposicion> peliculaDisposicion;

    // SECTION: Constructor

    @Builder
    public Ciudad(String nombre){
        this.nombre = nombre;
    }
}
