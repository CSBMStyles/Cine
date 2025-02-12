package com.unicine.entidades;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

import com.unicine.util.validaciones.anotaciones.MultiPattern;

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

    @NotBlank(message = "La dirección no puede estar vacía")
    @MultiPattern({
        @Pattern(regexp = ".{4,}", message = "La dirección debe tener al menos cuatro caracteres"),
        @Pattern(regexp = ".{1,100}", message = "La dirección no puede tener más de cien caracteres")
    })
    @Column(nullable = false, length = 100)
    private String direccion;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Pattern(regexp = "^.{10}$", message = "El teléfono debe tener exactamente diez caracteres")
    @Column(nullable = false, length = 20)
    private String telefono;

    // SECTION: Relaciones

    @NotNull(message = "La ciudad no puede estar vacía")
    @ManyToOne
    private Ciudad ciudad;

    @NotNull(message = "El administrador no puede estar vacío")
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
