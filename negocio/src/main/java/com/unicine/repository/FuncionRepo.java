package com.unicine.repository;

import com.unicine.entity.Funcion;
import com.unicine.entity.Horario;
import com.unicine.transfer.projetion.DetalleFuncionesProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionRepo extends JpaRepository<Funcion, Integer> {
    
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    // REVIEW: La razón de esta variable es para evitar escribir el nombre completo de la clase en la consulta es inutil para una sola consulta para para varios DTO es util
    String direccion = "com.unicine.transfer.data";

    /**
     * Consulta para obtener las funciones de una sala y horario
     * @param atributos: codigo de la sala, horario de la funcion
     * @return lista de funciones
     */
    @Query("select f from Sala s join s.funciones f where s.codigo = :codigoSala and f.horario = :horario")
    List<Funcion> obtenerFuncionesHorarioSala(Integer codigoSala, Horario horario);

    /**
     * Verifica si existe solapamiento de horarios en una sala
     * @param atributos: codigo de la sala, fecha de inicio, fecha de fin
     * @return funcion que se solapa
     */
    @Query("select f from Funcion f where f.sala.codigo = :codigoSala and not (f.horario.fechaFin <= :fechaInicio or f.horario.fechaInicio >= :fechaFin)")
    Optional<Funcion> solapaHorarioSala(Integer codigoSala, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Verifica si existe solapamiento de horarios en una sala excluyendo el horario actual
     * @param atributos: codigo de la sala, codigo del horario, fecha de inicio, fecha de fin
     * @return funcion que se solapa
     */
    @Query("select f from Funcion f join f.horario h join f.sala s where s.codigo = :codigoSala and h.codigo != :codigoHorario and not (h.fechaFin <= :fechaInicio or h.fechaInicio >= :fechaFin)")
    Optional<Funcion> solapaHorarioTeatro (Integer codigoSala, Integer codigoHorario, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Consulta para obtener las funciones de una sala
     * @param atributos: codigo de la sala
     * @return lista de funciones
     */
    @Query("select f from Funcion f where f.sala.codigo = :codigoSala")
    List<Funcion> listarFuncionesSala(Integer codigoSala);

    /**
     * Consulta para obtener las funciones de un teatro
     * @param atributos: codigo del teatro
     * @return lista de funciones
     */
    @Query("select f from Funcion f where f.sala.teatro.codigo = :codigoTeatro")
    List<Funcion> listarFuncionesTeatro(Integer codigoTeatro);

    /**
     * Consulta para obtener las funciones de una ciudad
     * @param atributos: codigo de la ciudad
     * @return lista de funciones
     */
    @Query("select f from Funcion f where f.sala.teatro.ciudad.codigo = :codigoCiudad")
    List<Funcion> listarFuncionesCiudad(Integer codigoCiudad);

    /**
     * Consulta para verificar la disponibilidad de una funcion
     * @param atributos: codigo de la funcion
     * @return funcion
     */
    @Query("select f from Funcion f where f.horario.codigo = :codigo")
    List<Funcion> verificarDisponibilidad(Integer codigo);

    /**
     * Consulta para obtener los detalles de las funciones de una pelicula
     * @param atributos: codigo de la pelicula
     * @return lista de detalles de funciones
     */
    @Query("select f.pelicula.nombre as nombrePelicula, " +
       "f.pelicula.estado as estadoPelicula, " +
       "f.pelicula.imagenes as imagenes, " +
       "f.sala.codigo as codigoSala, " +
       "f.sala.teatro.direccion as direccionTeatro, " +
       "f.sala.teatro.ciudad.nombre as nombreCiudad, " +
       "f.horario as horario " +
       "from Funcion f where f.pelicula.codigo = :codigoPelicula")
    List<DetalleFuncionesProjection> listarDetallesFunciones(Integer codigoPelicula);

    /**
     * Consulta para obtener las funciones de una pelicula en una ciudad para la disposicion
     * @param atributos: codigo de la pelicula, codigo de la ciudad, fecha actual
     * @return lista de funciones
     */
    @Query("select f from Funcion f join f.sala s join s.teatro t join t.ciudad c where f.pelicula.codigo = :peliculaId and c.codigo = :ciudadId and f.horario.fechaFin > :fechaActual")
    List<Funcion> buscarFuncionesActivasDisposicion(Integer peliculaId, Integer ciudadId, LocalDateTime fechaActual);

    /**
     * Consulta para obtener la primera funcion de una pelicula en una ciudad para la disposicion
     * @param atributos: codigo de la pelicula, codigo de la ciudad
     * @return lista de funciones
     */
    @Query("select f from Funcion f join f.sala s join s.teatro t join t.ciudad c where f.pelicula.codigo = :peliculaId and c.codigo = :ciudadId order by f.horario.fechaInicio ASC")
    List<Funcion> buscarPrimeraFuncionDisposicion(Integer peliculaId, Integer ciudadId);

    /**
     * #️⃣ Consulta para obtener las funciones con compras vacias de teatros especificos
     * @param atributos: codigo del teatro
     * @return lista de funciones
     */
    @Query("select f from Funcion f where f.compras is empty and f.sala.teatro.codigo = :codigoTeatro")
    List<Funcion> funcionesComprasVaciasTeatro(Integer codigoTeatro);

    /**
     * #️⃣ Consulta para obtener las funciones de un teatro en un rango de fechas usa el current date para tomar la fecha actual en fecha
     * @param atributos: codigo del teatro, fecha de inicio, fecha de fin
     * @return lista de funciones
     */
    @Query("select f from Funcion f where f.sala.teatro.codigo = :codigoTeatro and f.horario.fechaInicio between :fechaInicio and :fechaFin")
    List<Funcion> listarFuncionesTeatroFecha(Integer codigoTeatro, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
