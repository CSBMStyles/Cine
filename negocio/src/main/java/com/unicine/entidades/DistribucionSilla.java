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

    @ToString.Exclude
    @Column(nullable = true, columnDefinition = "json")
    private String esquemaTemporal;

    @NotNull(message = "El total de sillas no puede estar vacío")
    @PositiveOrZero(message = "El total de sillas debe ser un número positivo")
    @Column(nullable = false)
    private Integer totalSillas;

    @NotNull(message = "El número de filas no puede estar vacío")
    @PositiveOrZero(message = "El número de filas debe ser un número positivo")
    @Column(nullable = false)
    private Integer filas;

    @NotNull(message = "El número de columnas no puede estar vacío")
    @PositiveOrZero(message = "El número de columnas debe ser un número positivo")
    @Column(nullable = false)
    private Integer columnas;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "distribucionSilla", cascade = CascadeType.ALL)
    private List<Sala> salas;
    
    // SECTION: Constructor

    @Builder
    public DistribucionSilla(String esquema, String esquemaTemporal, Integer totalSillas, Integer filas, Integer columnas) {
        this.esquema = esquema;
        this.esquemaTemporal = esquemaTemporal;
        this.totalSillas = totalSillas;
        this.filas = filas;
        this.columnas = columnas;
    }
}
