package com.unicine.entity.theater;

import com.unicine.entity.user.AdministradorTeatro;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
import com.unicine.util.validation.catalog.ValidationMessages;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Teatro implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotBlank(message = ValidationMessages.ADDRESS_NOT_BLANK)
    @Size(min = 4, max = 100, message = ValidationMessages.ADDRESS_SIZE_MIN_FOUR)
    @Column(nullable = false, length = 100)
    private String direccion;

    @NotBlank(message = ValidationMessages.THEATER_PHONE_NOT_BLANK)
    @Pattern(regexp = "^.{10}$", message = ValidationMessages.PHONE_SIZE_EXACT_TEN)
    @Column(nullable = false, length = 20)
    private String telefono;

    // SECTION: Relaciones

    @NotNull(message = ValidationMessages.THEATER_CITY_NOT_NULL)
    @ManyToOne
    private Ciudad ciudad;

    @NotNull(message = ValidationMessages.THEATER_ADMIN_NOT_NULL)
    @ManyToOne
    private AdministradorTeatro administradorTeatro;

    @ToString.Exclude
    @OneToMany(mappedBy = "teatro", cascade = CascadeType.ALL)
    private List<Sala> salas;
    
    // SECTION: Constructor

    @Builder
    public Teatro(String direccion, String telefono, Ciudad ciudad, AdministradorTeatro administradorTeatro) {
        this.direccion = direccion;
        this.telefono = telefono;
        this.ciudad = ciudad;
        this.administradorTeatro = administradorTeatro;
    }
}
