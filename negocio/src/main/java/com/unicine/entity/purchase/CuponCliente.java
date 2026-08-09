package com.unicine.entity.purchase;

import com.unicine.entity.user.Cliente;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
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
import com.unicine.util.validation.catalog.ValidationMessages;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CuponCliente implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull(message = ValidationMessages.CLIENT_COUPON_STATUS_NOT_NULL)
    @Column(nullable = false)
    private Boolean estado;

    // !SECTION
    // SECTION: Relaciones

    @ManyToOne
    @NotNull(message = ValidationMessages.CLIENT_COUPON_COUPON_NOT_NULL)
    @JoinColumn(nullable = false)
    private Cupon cupon;

    @ManyToOne
    @NotNull(message = ValidationMessages.CLIENT_COUPON_CLIENT_NOT_NULL)
    @JoinColumn(nullable = false)
    private Cliente cliente;

    @ToString.Exclude
    @OneToOne(mappedBy = "cuponCliente", cascade = CascadeType.ALL)
    private Compra compra;
    
    // !SECTION
    // SECTION: Constructor

    @Builder
    public CuponCliente(Boolean estado, Cupon cupon, Cliente cliente) {
        this.estado = estado;
        this.cupon = cupon;
        this.cliente = cliente;
    }
    // !SECTION
}
