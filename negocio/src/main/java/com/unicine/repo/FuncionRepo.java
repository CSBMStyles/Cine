package com.unicine.repo;

import com.unicine.dto.DetalleFuncionesDTO;
import com.unicine.entidades.Funcion;
import com.unicine.entidades.Horario;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionRepo extends JpaRepository<Funcion, Integer> {
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    // REVIEW: La razón de esta variable es para evitar escribir el nombre completo de la clase en la consulta es inutil para una sola consulta para para varios DTO es util
    String direccion = "com.unicine.dto";

    /**
     * Consulta para obtener las funciones de una sala y horario
     * @param atributos: codigo de la sala, horario de la funcion
     * @return lista de funciones
     */
    @Query("select f from Sala s join s.funciones f where s.codigo = :codigoSala and f.horario = :horario")
    List<Funcion> obtenerFuncionesHorarioSala(Integer codigoSala, Horario horario);

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
     * @return lista de detalles de funciones: nombre de la pelicula, estado de la pelicula, imagenes de la pelicula, codigo de la sala, direccion del teatro, nombre de la ciudad, horario de la funcion
     */
    @Query("select new " + direccion + ".DetalleFuncionesDTO( f.pelicula.nombre, f.pelicula.estado, f.pelicula.imagenes, f.sala.codigo, f.sala.teatro.direccion, f.sala.teatro.ciudad.nombre, f.horario ) from Funcion f where f.pelicula.codigo = :codigoPelicula")
    List<DetalleFuncionesDTO> listarDetallesFunciones(Integer codigoPelicula);

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
