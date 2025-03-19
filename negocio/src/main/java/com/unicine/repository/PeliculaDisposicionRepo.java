package com.unicine.repository;

import com.unicine.entity.PeliculaDisposicion;
import com.unicine.entity.composed.PeliculaDisposicionCompuesta;
import com.unicine.enumeration.EstadoPelicula;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PeliculaDisposicionRepo extends JpaRepository<PeliculaDisposicion, PeliculaDisposicionCompuesta> {
    
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    /**
     * Consulta que permite obtener el estado de la pelicula en una ciudad
     * @param atributos: codigo de la pelicula, codigo de la ciudad
     * @return disposicion de la pelicula
     */
    @Query("select pd from PeliculaDisposicion pd where pd.pelicula.codigo = :codigoPelicula and pd.ciudad.codigo = :codigoCiudad")
    Optional<PeliculaDisposicion> obtenerDisposicionCiudad(Integer codigoPelicula, Integer codigoCiudad);

    /**
     * Consulta para obtener las disposiciones de una película excluyendo un estado específico
     * @param atributo: codigo de la película, estado de la película
     * @return lista de disposiciones de la película
     */
    @Query("select pd from PeliculaDisposicion pd where pd.pelicula.codigo = :codigoPelicula and pd.estadoPelicula != :estadoExcluir")
    List<PeliculaDisposicion> listarDisposicionesPelicula(Integer codigoPelicula, EstadoPelicula estadoExcluir);

    /**
     * Consulta optimizada para obtener sólo las disposiciones que requieren actualización de estado
     * @param fechaActual Fecha y hora actual para comparación
     * @return Lista de disposiciones que pueden necesitar actualización de estado:
     * 1. Disposiciones en PREVENTA donde ya comenzó la primera función
     * 2. Disposiciones en ESTRENO donde han pasado {7} días desde la fecha de función inicial
     * 3. Cualquier disposición que pueda necesitar actualización basada en el estado de sus funciones
     */
    @Query("select pd from PeliculaDisposicion pd " +
           "where (pd.estadoPelicula = 'PREVENTA' " +
                 "and exists (select f from Funcion f " +
                             "join f.sala s " +
                             "join s.teatro t " +
                             "where f.pelicula.codigo = pd.pelicula.codigo " +
                             "and t.ciudad.codigo = pd.ciudad.codigo " +
                             "and f.horario.fechaInicio <= :fechaActual)) " +
           "or (pd.estadoPelicula = 'ESTRENO' " +
               "and pd.fechaFuncionInicial is not null " +
               "and FUNCTION('DATEDIFF', pd.fechaFuncionInicial, :fechaActual) >= 7)")
    List<PeliculaDisposicion> buscarDisposicionesActualizar(LocalDateTime fechaActual);

    /**
     * Consulta para obtener disposiciones con funciones programadas en un rango de
     * fechas
     * 
     * @param inicioRango Fecha de inicio del rango a buscar
     * @param finRango    Fecha de fin del rango a buscar
     * @return Lista de disposiciones que tienen funciones en ese rango
     */
    @Query("select distinct pd from PeliculaDisposicion pd " +
            "where exists (select f from Funcion f join f.sala s join s.teatro t " +
            "where f.pelicula.codigo = pd.pelicula.codigo and t.ciudad.codigo = pd.ciudad.codigo " +
            "and f.horario.fechaInicio between :inicioRango and :finRango)")
    List<PeliculaDisposicion> buscarDisposicionesFuncionesRango(LocalDateTime inicioRango, LocalDateTime finRango);

    
}
