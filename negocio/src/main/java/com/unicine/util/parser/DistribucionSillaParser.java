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
    private static final String CELDA_OCUPADA = "O";

    private final Gson gson;

    public String[][] parse(DistribucionSilla distribucionSilla) {
        if (distribucionSilla == null || distribucionSilla.getEsquema() == null) {
            throw new ResourceNotFoundException(TheaterErrorCatalog.DOMAIN_THEATER_ENTITY_SEAT_SCHEMA_NOT_FOUND);
        }

        return parse(distribucionSilla.getEsquema());
    }

    public String[][] parse(String esquemaJson) {
        if (esquemaJson == null) {
            throw new ResourceNotFoundException(TheaterErrorCatalog.DOMAIN_THEATER_ENTITY_SEAT_SCHEMA_NOT_FOUND);
        }

        try {
            return gson.fromJson(esquemaJson, String[][].class);
        } catch (JsonSyntaxException e) {
            throw new ValidationException(TheaterErrorCatalog.DOMAIN_THEATER_ENTITY_SEAT_SCHEMA_INVALID);
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

    public String[][] marcarSillaOcupada(String[][] esquema, Integer fila, Integer columna) {
        if (!existeSilla(esquema, fila, columna)) {
            throw new ResourceNotFoundException(TheaterErrorCatalog.DOMAIN_THEATER_BUSINESS_RULE_SEAT_NOT_FOUND_IN_ROOM_DISTRIBUTION);
        }

        esquema[fila - 1][columna - 1] = CELDA_OCUPADA;
        return esquema;
    }

    public String[][] marcarSillaDisponible(String[][] esquema, Integer fila, Integer columna) {
        if (!existeSilla(esquema, fila, columna)) {
            throw new ResourceNotFoundException(TheaterErrorCatalog.DOMAIN_THEATER_BUSINESS_RULE_SEAT_NOT_FOUND_IN_ROOM_DISTRIBUTION);
        }

        esquema[fila - 1][columna - 1] = CELDA_DISPONIBLE;
        return esquema;
    }

    public String toJson(String[][] esquema) {
        return gson.toJson(esquema);
    }
}
