package com.unicine.entity.purchase;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
import com.unicine.entity.showing.Funcion;
import com.unicine.util.validation.catalog.ValidationMessages;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Entrada implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull(message = ValidationMessages.TICKET_PRICE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.TICKET_PRICE_POSITIVE)
    @Column(nullable = false)
    private Double precio;

    @NotNull(message = ValidationMessages.TICKET_ROW_NOT_NULL)
    @Positive(message = ValidationMessages.TICKET_ROW_POSITIVE)
    @Column(nullable = false)
    private Integer fila;

    @NotNull(message = ValidationMessages.TICKET_COLUMN_NOT_NULL)
    @Positive(message = ValidationMessages.TICKET_COLUMN_POSITIVE)
    @Column(nullable = false)
    private Integer columna;

    // SECTION: Relaciones

    @ManyToOne
    @NotNull(message = ValidationMessages.TICKET_PURCHASE_NOT_NULL)
    private Compra compra;

    @ManyToOne
    @NotNull(message = ValidationMessages.TICKET_SHOWING_NOT_NULL)
    private Funcion funcion;

    // SECTION: Constructor

    @Builder
    public Entrada(Double precio, Integer fila, Integer columna, Compra compra, Funcion funcion) {
        this.precio = precio;
        this.fila = fila;
        this.columna = columna;
        this.compra = compra;
        this.funcion = funcion;
    }
}
