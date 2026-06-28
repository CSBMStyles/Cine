package com.unicine.entity.user;

import com.unicine.entity.image.Imagen;
import com.unicine.entity.movie.Coleccion;
import com.unicine.entity.purchase.CuponCliente;
import com.unicine.entity.purchase.Compra;
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

import com.unicine.entity.image.interfaced.Imagenable;
import com.unicine.util.validation.catalog.ValidationMessages;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Cliente extends Persona implements Serializable, Imagenable {

    // SECTION: Atributos

    @NotNull(message = ValidationMessages.CLIENT_STATUS_NOT_NULL)
    @Column(nullable = false)
    private Boolean estado;

    @NotNull(message = ValidationMessages.BIRTH_DATE_NOT_NULL)
    @Past(message = ValidationMessages.CLIENT_BIRTH_DATE_PAST)
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Size(max = 5, message = ValidationMessages.PHONE_LIST_MAX_FIVE)
    @ElementCollection
    @Column(nullable = true, length = 20)
    private List<@Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = ValidationMessages.INVALID_PHONE_FORMAT) String> telefonos;

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
        this.estado = estado;
        this.fechaNacimiento = fechaNacimiento;
        this.telefonos = telefonos;
    }

    @Override
    public String getCarpetaPrefijo() {
        return "clientes";
    }
}