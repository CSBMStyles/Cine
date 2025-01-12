package com.unicine.util.validacion.atributos;

import com.unicine.util.validacion.anotaciones.MultiPattern;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PersonaAtributoValidator {
    
    @NotBlank(message = "La cédula no puede estar vacía")
    @MultiPattern({
        @Pattern(regexp = "^[1-9]\\d*$", message = "La cédula debe ser un número positivo"),
        @Pattern(regexp = "\\d{7,}", message = "La cédula debe tener al menos siete dígitos"),
        @Pattern(regexp = "\\d{1,10}", message = "La cédula no puede tener más de diez dígitos")
    })
    private String cedula;
}
