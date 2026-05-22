package com.unicine.entity.theater;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "El esquema no puede estar en blanco")
    @Column(nullable = false, columnDefinition = "json")
    private String esquema;

    @NotNull(message = "El total de sillas no puede estar vacío")
    @PositiveOrZero(message = "El total de sillas debe ser un número positivo o cero")
    @Column(nullable = false)
    private Integer totalSillas;

    @NotNull(message = "El número de filas no puede estar vacío")
    @PositiveOrZero(message = "El número de filas debe ser un número positivo o cero")
    @Column(nullable = false)
    private Integer filas;

    @NotNull(message = "El número de columnas no puede estar vacío")
    @PositiveOrZero(message = "El número de columnas debe ser un número positivo o cero")
    @Column(nullable = false)
    private Integer columnas;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "distribucionSilla", cascade = CascadeType.ALL)
    private List<Sala> salas;
    
    // SECTION: Constructor

    @Builder
    public DistribucionSilla(String esquema, Integer totalSillas, Integer filas, Integer columnas) {
        this.esquema = esquema;
        this.totalSillas = totalSillas;
        this.filas = filas;
        this.columnas = columnas;
    }
}
