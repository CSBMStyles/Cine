package com.unicine.entity.theater;

import com.unicine.entity.showing.Funcion;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.List;

import com.unicine.enums.theater.TipoSala;
import com.unicine.util.validation.catalog.ValidationMessages;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Sala implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotBlank(message = ValidationMessages.ROOM_NAME_NOT_BLANK)
    @Size(max = 100, message = ValidationMessages.NAME_SIZE_MAX_HUNDRED)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull(message = ValidationMessages.ROOM_TYPE_NOT_NULL)
    @Column (nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TipoSala tipoSala;

    // !SECTION
    // SECTION: Relaciones

    @ManyToOne
    @NotNull(message = ValidationMessages.ROOM_THEATER_NOT_NULL)
    private Teatro teatro;

    @ManyToOne
    @NotNull(message = ValidationMessages.ROOM_DISTRIBUTION_NOT_NULL)
    private DistribucionSilla distribucionSilla;

    @ToString.Exclude
    @OneToMany(mappedBy = "sala",cascade = CascadeType.ALL)
    private List<Funcion> funciones;
    
    // !SECTION
    // SECTION: Constructor

    @Builder
    public Sala(String nombre, TipoSala tipoSala, Teatro teatro, DistribucionSilla distribucionSilla) {
        this.nombre = nombre;
        this.tipoSala = tipoSala;
        this.teatro = teatro;
        this.distribucionSilla = distribucionSilla;
    }
    // !SECTION
}
