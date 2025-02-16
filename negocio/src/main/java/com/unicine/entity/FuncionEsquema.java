package com.unicine.entity;

import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FuncionEsquema implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @Column(nullable = true, columnDefinition = "json")
    private String esquemaTemporal;

    @PositiveOrZero(message = "El número de sillas ocupadas debe ser un número positivo o cero")
    @Column(nullable = false)
    private Integer ocupadas;

    @PositiveOrZero(message = "El número de sillas disponibles debe ser un número positivo o cero")
    @Column(nullable = false)
    private Integer disponibles;

    @PositiveOrZero(message = "El número de sillas en mantenimiento debe ser un número positivo o cero")
    @Column(nullable = false)
    private Integer mantenimiento;

    // SECTION: Relaciones

    @NotNull(message = "La función no estar vacía")
    @OneToOne
    private Funcion funcion;
    
    // SECTION: Constructor

    @Builder
    public FuncionEsquema(Funcion funcion) {
        this.funcion = funcion;
    }
}
