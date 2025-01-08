package com.unicine.util.validacion.atributos;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DistribucionAttributeValidator {

    @Pattern(regexp = "^[1-9]\\d*$", message = "El código de la distribución debe ser un número positivo")
    private String codigo;
}
