package com.unicine.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.unicine.entity.interfa.Imagenable;
import com.unicine.util.validaciones.anotaciones.MultiPattern;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Cliente extends Persona implements Serializable, Imagenable {

    // SECTION: Atributos

    @NotNull(message = "El estado no puede estar vacío")
    @Column(nullable = false)
    private Boolean estado;

    @NotNull(message = "El apellido no puede estar vacío")
    @Past(message = "La fecha de nacimiento debe estar en el pasado")
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Size(max = 5, message = "El teléfono no puede tener más de cinco telefonos")
    @MultiPattern({
        @Pattern(regexp = "^[0-9]+$", message = "El teléfono solo puede contener números"),
        @Pattern(regexp = "^.{10}$", message = "El teléfono debe tener exactamente diez caracteres")
    })
    @ElementCollection
    @Column(nullable = true, length = 20)
    private List<String> telefonos;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Compra> compras;

    @ToString.Exclude
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<CuponCliente> cuponClientes;

    @ToString.Exclude
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Coleccion> colecciones;

    @ToString.Exclude
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Imagen imagen;

    // SECTION: Constructor

    @Builder
    public Cliente(Integer cedula, String nombre, String apellido, String correo, String password, Boolean estado, LocalDate fechaNacimiento, List<String> telefonos) {
        super(cedula, nombre, apellido, correo, password);
        this.estado = false;
        this.fechaNacimiento = fechaNacimiento;
        this.telefonos = telefonos;
    }

    @Override
    public String getFolderPrefix() {
        return "clientes";
    }
}