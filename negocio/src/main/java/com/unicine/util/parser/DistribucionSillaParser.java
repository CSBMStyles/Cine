package com.unicine.util.parser;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.unicine.entity.theater.DistribucionSilla;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.exception.ValidationException;
import com.unicine.util.validation.catalog.domain.TheaterErrorCatalog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistribucionSillaParser {

    private static final String CELDA_DISPONIBLE = "D";

    private final Gson gson;

    public String[][] parse(DistribucionSilla distribucionSilla) {
        if (distribucionSilla == null || distribucionSilla.getEsquema() == null) {
            throw new ResourceNotFoundException(TheaterErrorCatalog.ENT017);
        }

        try {
            return gson.fromJson(distribucionSilla.getEsquema(), String[][].class);
        } catch (JsonSyntaxException e) {
            throw new ValidationException(TheaterErrorCatalog.ENT018);
        }
    }

    public boolean existeSilla(String[][] esquema, Integer fila, Integer columna) {
        if (esquema == null || fila == null || columna == null) {
            return false;
        }

        int indiceFila = fila - 1;
        int indiceColumna = columna - 1;

        return indiceFila >= 0 && indiceFila < esquema.length
                && indiceColumna >= 0 && indiceColumna < esquema[indiceFila].length;
    }

    public boolean esSillaDisponible(String[][] esquema, Integer fila, Integer columna) {
        if (!existeSilla(esquema, fila, columna)) {
            return false;
        }

        return CELDA_DISPONIBLE.equals(esquema[fila - 1][columna - 1]);
    }
}
