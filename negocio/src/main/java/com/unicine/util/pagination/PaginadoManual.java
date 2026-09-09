package com.unicine.util.pagination;

import java.util.List;

/**
 * Paginado en memoria para respuestas ya filtradas.
 * Se usa cuando el filtrado ocurre sobre DTOs (ej: cartelera por ciudad/fecha)
 * donde el repositorio no puede paginar directo.
 */
public final class PaginadoManual {

    private PaginadoManual() {
    }

    /**
     * Recorta la lista segun page/size. Sin params devuelve la lista intacta.
     */
    public static <T> List<T> paginar(List<T> lista, Integer page, Integer size) {
        if (page == null && size == null) {
            return lista;
        }
        int p = page != null ? page : 0;
        int s = size != null ? size : 10;
        int from = Math.min(p * s, lista.size());
        int to = Math.min(from + s, lista.size());
        return lista.subList(from, to);
    }
}
