package com.unicine.entity.confiteria;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.unicine.enums.confiteria.UnidadMedida;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Representa una presentacion comercial de un producto de confiteria.
 * 
 * Una confiteria (por ejemplo, "Agua") puede tener multiples presentaciones
 * (500 ml, 1 L) con precios independientes.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ConfiteriaPresentacion implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRESENTATION_PORTION_NOT_NULL)
    @Positive(message = ValidationMessages.CONFECTIONERY_PRESENTATION_PORTION_POSITIVE)
    @Column(nullable = false)
    private Double porcion;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRESENTATION_UNIT_NOT_NULL)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UnidadMedida unidadMedida;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRESENTATION_PRICE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.CONFECTIONERY_PRESENTATION_PRICE_POSITIVE_OR_ZERO)
    @Column(nullable = false)
    private Double precio;

    @PositiveOrZero(message = ValidationMessages.CONFECTIONERY_PRESENTATION_BASE_PRICE_POSITIVE_OR_ZERO)
    @Column
    private Double precioBase;

    @Column
    private LocalDateTime fechaExpiracionTemporal;

    // SECTION: Relaciones

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRESENTATION_CONFECTIONERY_NOT_NULL)
    @ManyToOne
    @JoinColumn(nullable = false)
    private Confiteria confiteria;

    // SECTION: Constructor

    @Builder
    public ConfiteriaPresentacion(Double porcion, UnidadMedida unidadMedida, Double precio,
                                  Double precioBase, LocalDateTime fechaExpiracionTemporal,
                                  Confiteria confiteria) {
        this.porcion = porcion;
        this.unidadMedida = unidadMedida;
        this.precio = precio;
        this.precioBase = precioBase;
        this.fechaExpiracionTemporal = fechaExpiracionTemporal;
        this.confiteria = confiteria;
    }

    /**
     * Indica si el precio actual es un descuento temporal activo.
     */
    public boolean esPrecioTemporal() {
        return precio < precioBase;
    }

    /**
     * Calcula el porcentaje de descuento contra el precio base.
     * 
     * @return porcentaje redondeado a entero, o 0 si no aplica.
     */
    public Integer calcularPorcentajeDescuento() {
        if (!esPrecioTemporal() || precioBase == 0) {
            return 0;
        }
        double porcentaje = (precioBase - precio) / precioBase * 100;
        return (int) Math.round(porcentaje);
    }

    // !SECTION
}
