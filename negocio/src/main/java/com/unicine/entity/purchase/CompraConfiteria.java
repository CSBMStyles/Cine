package com.unicine.entity.purchase;

import com.unicine.entity.confiteria.Confiteria;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class CompraConfiteria implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull(message = ValidationMessages.PRICE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.PRICE_POSITIVE_OR_ZERO)
    @Column(nullable = false)
    private Double precio;

    @NotNull(message = ValidationMessages.UNITS_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.UNITS_POSITIVE_OR_ZERO)
    @Column(nullable = false)
    private Integer unidades;

    // SECTION: Relaciones

    @ManyToOne
    // TODO: No hay constante específica para ValidationMessages.TICKET_PURCHASE_NOT_NULL en CompraConfiteria
    @NotNull(message = ValidationMessages.TICKET_PURCHASE_NOT_NULL)
    @JoinColumn(nullable = false)
    private Compra compra;

    @ManyToOne
    @NotNull(message = ValidationMessages.FIELD_REQUIRED)
    @JoinColumn(nullable = false)
    private Confiteria confiteria;
    
    // SECTION: Constructor

    @Builder
    public CompraConfiteria(Double precio, Integer unidades, Compra compra, Confiteria confiteria) {
        this.precio = precio;
        this.unidades = unidades;
        this.compra = compra;
        this.confiteria = confiteria;
    }
    
}
