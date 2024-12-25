package com.unicine.entidades;

/* NOTE: La clase Persona funciona como padre para la clase Administrador, AdministradorTeatro, y Cliente. Esta no se refleja como entidad por la estategia aplicada en las hijas para la herencia */

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@MappedSuperclass
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Persona {

    // SECTION: Atributos

    @Id
    @NotNull(message = "La cédula no puede estar vacía")
    @Positive(message = "La cédula debe ser un número positivo")
    @Column(length = 10)
    @EqualsAndHashCode.Include
    private Integer cedula;

    @NotBlank(message = "El nombre no puede estar en blanco")
    @Size(max = 50, message = "El nombre no puede tener más de cincuenta caracteres")
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar en blanco")
    @Size(max = 50, message = "El nombre no puede tener más de cincuenta caracteres")
    @Column(nullable = false, length = 50)
    private String apellido;

    @NotBlank(message = "El correo no puede estar vacio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 150, message = "El correo no puede tener más de cincuenta caracteres")
    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @ToString.Exclude
    @NotBlank(message = "La contraseña no puede estar en blanco")
    @Size(min = 6, max = 200, message = "La contraseña debe estar entre seis y doscientos caracteres")
    @Column(nullable = false, length = 200)
    private String password;

    // SECTION: Constructor

    public Persona(Integer cedula, String nombre, String apellido, String correo, String password) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.password = password;
    }
}
