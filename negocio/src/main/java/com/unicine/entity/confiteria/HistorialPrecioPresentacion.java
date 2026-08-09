package com.unicine.entity.confiteria;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.unicine.enums.confiteria.TipoCambioPrecioPresentacion;
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
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Registra cada cambio de precio de una presentacion de confiteria.
 * 
 * Solo se almacenan descuentos temporales y aumentos. Los cambios que no
 * modifican el precio no generan historial.
 * 
 * PERMISOS:
 * - Cliente y administrador de teatro: solo consulta del ultimo registro
 *   para visualizar el porcentaje de descuento.
 * - Administrador: consulta completa y eliminacion de historial.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HistorialPrecioPresentacion implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_PREVIOUS_PRICE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_PREVIOUS_PRICE_POSITIVE_OR_ZERO)
    @Column(nullable = false)
    private Double precioAnterior;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_NEW_PRICE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_NEW_PRICE_POSITIVE_OR_ZERO)
    @Column(nullable = false)
    private Double precioNuevo;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_TYPE_NOT_NULL)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoCambioPrecioPresentacion tipoCambio;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_PERCENTAGE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_PERCENTAGE_POSITIVE_OR_ZERO)
    @Column(nullable = false)
    private Integer porcentaje;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_DATE_NOT_NULL)
    @Column(nullable = false)
    private LocalDateTime fechaCambio;

    // !SECTION
    // SECTION: Relaciones

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_PRESENTATION_NOT_NULL)
    @ManyToOne
    @JoinColumn(nullable = false)
    private ConfiteriaPresentacion presentacion;

    // !SECTION
    // SECTION: Constructor

    @Builder
    public HistorialPrecioPresentacion(Double precioAnterior, Double precioNuevo,
                                        TipoCambioPrecioPresentacion tipoCambio, Integer porcentaje,
                                        LocalDateTime fechaCambio, ConfiteriaPresentacion presentacion) {
        this.precioAnterior = precioAnterior;
        this.precioNuevo = precioNuevo;
        this.tipoCambio = tipoCambio;
        this.porcentaje = porcentaje;
        this.fechaCambio = fechaCambio;
        this.presentacion = presentacion;
    }

    // !SECTION
}
