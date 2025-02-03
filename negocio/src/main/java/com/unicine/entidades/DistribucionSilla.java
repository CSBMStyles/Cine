package com.unicine.entidades;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.persistence.CascadeType;
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
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DistribucionSilla implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull(message = "El esquema no puede estar vacio")
    @Column(nullable = false, columnDefinition = "json")
    private String esquema;

    @PositiveOrZero(message = "El total de sillas debe ser un número positivo")
    @Column(nullable = false)
    private Integer totalSillas;

    @PositiveOrZero(message = "El número de filas debe ser un número positivo")
    @Column(nullable = false)
    private Integer filas;

    @PositiveOrZero(message = "El número de columnas debe ser un número positivo")
    @Column(nullable = false)
    private Integer columnas;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "distribucionSilla", cascade = CascadeType.ALL)
    private List<Sala> salas;
    
    // SECTION: Constructor

    @Builder
    public DistribucionSilla(String esquema) {
        this.esquema = esquema;
    }
}
