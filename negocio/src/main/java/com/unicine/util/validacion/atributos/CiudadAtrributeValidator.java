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
public class CiudadAtrributeValidator {

    @NotBlank(message = "El nombre de la ciudad no puede estar vacío")
    @MultiPattern({
        @Pattern(regexp = ".{2,}", message = "El nombre de la ciudad no debe ser menor a dos caracteres"),
        @Pattern(regexp = ".{1,100}", message = "El nombre de la ciudad no debe pasar los cien caracteres"),
        @Pattern(regexp = "^[a-zA-Z ]+$", message = "El nombre de la ciudad solo puede contener letras y espacios")
    })
    private String nombre;
}
