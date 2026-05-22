package com.unicine.entity.purchase;

import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
import java.time.LocalDateTime;
import com.unicine.util.validation.catalog.ValidationMessages;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cupon implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @Lob
    @NotBlank(message = ValidationMessages.COUPON_DESCRIPTION_NOT_BLANK)
    @Column(nullable = false, columnDefinition = "text")
    private String descripcion;

    @NotNull(message = ValidationMessages.COUPON_DISCOUNT_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.DISCOUNT_POSITIVE_OR_ZERO)
    @Max(value = 100, message = ValidationMessages.DISCOUNT_MAX_TOTAL)
    @Column(nullable = false)
    private Double descuento;

    @NotBlank(message = ValidationMessages.COUPON_CRITERION_NOT_BLANK)
    @Size(max = 100, message = ValidationMessages.CRITERION_SIZE_MAX_100)
    @Column(nullable = false, length = 100)
    private String criterio;

    @NotNull(message = ValidationMessages.COUPON_EXPIRY_NOT_NULL)
    @FutureOrPresent(message = ValidationMessages.COUPON_EXPIRY_FUTURE)
    @Column(nullable = false)
    private LocalDateTime fechaVencimiento;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "cupon", cascade =  CascadeType.ALL)
    private List<CuponCliente> cuponClientes;
    
    // SECTION: Constructor

    @Builder
    public Cupon(String descripcion, Double descuento, String criterio, LocalDateTime fechaVencimiento) {
        this.descripcion = descripcion;
        this.descuento = descuento;
        this.criterio = criterio;
        this.fechaVencimiento = fechaVencimiento;
    }
}
