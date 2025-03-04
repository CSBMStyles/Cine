package com.unicine.util.validation.attributes;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DistribucionAtributoValidator {

    @Pattern(regexp = "^[1-9]\\d*$", message = "El código de la distribución debe ser un número positivo, los caracteres no son permitidos")
    private String codigo;
}
