package com.unicine.entity.user;

/* NOTE: La clase Persona funciona como padre para la clase Administrador, AdministradorTeatro, y Cliente. Esta no se refleja como entidad por la estategia aplicada en las hijas para la herencia */

import jakarta.persistence.Id;

import com.unicine.util.validation.catalog.ValidationMessages;
import com.unicine.util.validation.group.OnCreate;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @NotNull(message = ValidationMessages.CEDULA_NOT_NULL)
    @Positive(message = ValidationMessages.CEDULA_POSITIVE)
    @Column(length = 10)
    @EqualsAndHashCode.Include
    private Integer cedula;

    @NotBlank(message = ValidationMessages.NAME_NOT_BLANK)
    @Size(max = 50, message = ValidationMessages.NAME_SIZE_MAX_FIFTY)
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = ValidationMessages.NAME_NOT_BLANK)
    @Size(max = 50, message = ValidationMessages.NAME_SIZE_MAX_FIFTY)
    @Column(nullable = false, length = 50)
    private String apellido;

    @NotBlank(message = ValidationMessages.EMAIL_NOT_NULL)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    @Size(max = 150, message = ValidationMessages.EMAIL_SIZE_MAX_ONE_HUNDRED_FIFTY)
    @Column(nullable = false, length = 150)
    private String correo;

    @ToString.Exclude
    @NotBlank(
        message = ValidationMessages.PASSWORD_NOT_BLANK,
        groups = OnCreate.class
    )
    @Size(min = 8, max = 200, message = ValidationMessages.PASSWORD_SIZE_MIN_EIGHT, groups = OnCreate.class)
    @Pattern.List({
        @Pattern(
            regexp = "^(?=.*[A-Z]).+$",
            message = ValidationMessages.PASSWORD_UPPERCASE,
            groups = OnCreate.class
        ),
        @Pattern(
            regexp = "^(?=.*[a-z]).+$",
            message = ValidationMessages.PASSWORD_LOWERCASE,
            groups = OnCreate.class
        ),
        @Pattern(
            regexp = "^(?=.*\\d).+$",
            message = ValidationMessages.PASSWORD_DIGIT,
            groups = OnCreate.class
        ),
        @Pattern(
            regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$",
            message = ValidationMessages.PASSWORD_SPECIAL,
            groups = OnCreate.class
        )
    })
    @Column(nullable = false, length = 200)
    private String password;

    // !SECTION
    // SECTION: Constructor

    public Persona(Integer cedula, String nombre, String apellido, String correo, String password) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.password = password;
    }
    // !SECTION
}
