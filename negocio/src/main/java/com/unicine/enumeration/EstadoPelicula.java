package com.unicine.enumeration;

public enum EstadoPelicula {
    PENDIENTE,        // La pelicula ha sido creada para luego ser asignada a una un grupo o todas las ciudad ese es su estado inicial       
    PREVENTA,          // Cuando una pelicula ha sido asignada a una funcion y su estado anterior era la `PENDINTE`s
    ESTRENO,           // Cuando la pelicula llega a la fecha de la presentacion de la funcion y su estado anterior era `PREVENTA` este es su estado hasta una duracion de siete {7} dias extactos osea se estreno el dia 10/03/2025T11:00 entonces el estado es `ESTRENO` siguiendo las condiciones y cuando llegue el 17/03/2025T11:00 mismo minuto y hora o en caso de que finalice la pelicula y no haya funciones activas en la ciudad, el estado cambia a `FUERA_CARTELERA` y en caso de que si hayan funciones activas en la ciudad para esa pelicula el estado será `CARTELERA``
    CARTELERA,         // Luego de que la pelicula haya pasado por la etapa de `ESTRENO` este es su estado `CARTELERA` en caso de que en la ciudad haya al menos una funcion activa de la pelicula, sino pasa a `FUERA_CARTELERA`
    FUERA_CARTELERA    // En caso de que una pelicula no tenga funciones activas en la ciudad este es su estado, pero en caso de que mas adelante se programe una funcion para la pelicula el estado vuelve a `CARTELERA` o `ESTRENO` dependiendo si aun no han pasado los siten dias desde la primera funcion, hasta que finalice la funcion y comprobar que no haya funciones activas en la ciudad
}
