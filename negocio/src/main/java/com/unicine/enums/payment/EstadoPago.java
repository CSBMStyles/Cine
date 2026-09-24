package com.unicine.enums.payment;

// State-lite para el puente con Mercado Pago: el enum guarda las
// transiciones validas sin clases de estado porque el flujo es fijo
// (PENDIENTE -> EN_VERIFICACION -> PAGADA) y Chain of Responsibility sobra.
public enum EstadoPago {
    PENDIENTE,
    EN_VERIFICACION,
    PAGADA,
    FALLIDA,
    EXPIRADA;

    // Indica si el estado actual puede pasar al destino sin saltarse el flujo.
    // Los estados finales nunca avanzan: el reintento crea una fila nueva.
    public boolean puedePasarA(EstadoPago destino) {
        if (destino == null || this == destino) {
            return false;
        }
        return switch (this) {
            case PENDIENTE -> destino == EN_VERIFICACION
                    || destino == FALLIDA
                    || destino == EXPIRADA;
            case EN_VERIFICACION -> destino == PAGADA
                    || destino == FALLIDA
                    || destino == EXPIRADA;
            case PAGADA, FALLIDA, EXPIRADA -> false;
        };
    }
}
