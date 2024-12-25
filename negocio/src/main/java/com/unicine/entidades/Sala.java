package com.unicine.entidades;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Sala implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de cien caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El tipo de sala no puede estar vacío")
    @Column (nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TipoSala tipoSala;

    // SECTION: Relaciones

    @ManyToOne
    private Teatro teatro;

    @ManyToOne
    private DistribucionSilla distribucionSilla;

    @ToString.Exclude
    @OneToMany(mappedBy = "sala",cascade = CascadeType.ALL)
    private List<Funcion> funciones;
    
    // SECTION: Constructor

    @Builder
    public Sala(String nombre, TipoSala tipoSala, Teatro teatro, DistribucionSilla distribucionSilla) {
        this.nombre = nombre;
        this.tipoSala = tipoSala;
        this.teatro = teatro;
        this.distribucionSilla = distribucionSilla;
    }
}
