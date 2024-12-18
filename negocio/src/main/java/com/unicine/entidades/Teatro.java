package com.unicine.entidades;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @NotNull(message = "La dirección no puede estar vacía")
    @Size(min = 1, max = 100, message = "La dirección debe tener al menos un caracter hasta cien")
    @Column(nullable = false, length = 100)
    private String direccion;

    @NotNull(message = "El teléfono no puede estar vacío")
    @Pattern(regexp = "^.{10}$", message = "El teléfono debe tener exactamente diez caracteres")
    @Column(nullable = false, length = 20)
    private String telefono;

    // SECTION: Relaciones

    @ManyToOne
    private Ciudad ciudad;

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
