package com.unicine.entity.showing;

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
import com.unicine.util.validation.catalog.ValidationMessages;
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

    @NotNull(message = ValidationMessages.SHOWING_SCHEMA_OCCUPIED_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SHOWING_SCHEMA_OCCUPIED_POSITIVE)
    @Column(nullable = false)
    private Integer ocupadas;

    @NotNull(message = ValidationMessages.SHOWING_SCHEMA_AVAILABLE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SHOWING_SCHEMA_AVAILABLE_POSITIVE)
    @Column(nullable = false)
    private Integer disponibles;

    @NotNull(message = ValidationMessages.SHOWING_SCHEMA_MAINTENANCE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SHOWING_SCHEMA_MAINTENANCE_POSITIVE)
    @Column(nullable = false)
    private Integer mantenimiento;

    // SECTION: Relaciones

    @NotNull(message = ValidationMessages.SHOWING_SCHEMA_SHOWING_NOT_NULL)
    @OneToOne
    private Funcion funcion;
    
    // SECTION: Constructor

    @Builder
    public FuncionEsquema(Funcion funcion) {
        this.funcion = funcion;
    }
}
