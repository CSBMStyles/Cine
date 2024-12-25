package com.unicine.repo;

import com.unicine.dto.DetallePeliculaHorarioDTO;
import com.unicine.entidades.EstadoPelicula;
import com.unicine.entidades.Pelicula;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PeliculaRepo extends JpaRepository<Pelicula, Integer> {
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    // REVIEW: La razón de esta variable es para evitar escribir el nombre completo de la clase en la consulta es inutil para una sola consulta para para varios DTO es util
    String direccion = "com.unicine.dto";

    /**
     * Consulta para obtener una pelicula por su nombre
     * @param atributo: nombre de la pelicula
     * @return pelicula
     */
    @Query("select p from Pelicula p where p.nombre = :nombrePelicula")
    Optional<Pelicula> obtenerPeliculaNombre(String nombrePelicula);

    /**
     * Consulta para obtener una pelicula de una funcion por su codigo
     * @param atributo: codigo de la pelicula
     * @return pelicula
     */
    @Query("select f.pelicula from Funcion f where  f.codigo = :codigoPelicula")
    Optional<Pelicula> obtenerPeliculaFuncion(Integer codigoPelicula);

    /**
     * Consulta para obtener las peliculas de una funcion por su nombre
     * @param atributo: nombre del genero
     * @return lista de peliculas
     */
    @Query("select p, f from Pelicula p left join p.funciones f where p.nombre like concat('%',:nombre,'%') ")
    List<Object[]> buscarPeliculasFuncion(String nombre);

    /**
     * Consulta para obtener las peliculas de una funcion a partir de su fecha en el horario
     * @param atributo: codigo de la pelicula, fecha del horario
     * @return lista de elementos con nombre de la pelicula, codigo de la funcion y horario
     */
    @Query("select p.nombre, f.codigo, h from Funcion f join f.horario h join f.pelicula p where p.codigo = :codigoPelicula and function('DATE', h.fechaInicio) = :fecha")
    List<Object[]> obtenerPeliculaFecha(Integer codigoPelicula, LocalDate fecha);

    /**
     * Consulta para obtener las peliculas de una ciudad y estado
     * @param atributo: codigo de la ciudad, estado de la pelicula
     * @return lista de peliculas
     */
    @Query("select distinct f.pelicula from Funcion f where f.sala.teatro.ciudad.codigo = :codigoCiudad and f.pelicula.estado = :estadoPelicula")
    List<Pelicula> listarPeliculasCuidadEstado(Integer codigoCiudad, EstadoPelicula estadoPelicula);

    /**
     * - Consulta para obtener las peliculas de una ciudad apartir de su nombre
     * @param atributo: codigo de la ciudad
     * @return lista de peliculas
     */
    @Query("select distinct f.pelicula from Funcion f where f.pelicula.nombre like concat('%',:nombrePelicula,'%') and f.sala.teatro.ciudad.codigo = :codigoCiudad")
    List<Pelicula> listarPeliculasNombreCuidad(String nombrePelicula, Integer codigoCiudad);

    /**
     * Consulta para obtener las peliculas de un estado
     * @param atributo: estado de la pelicula
     * @return lista de peliculas
     */
    @Query("select distinct f.pelicula from Funcion f where f.pelicula.estado = :estadoPelicula")
    List<Pelicula> listarPeliculasEstado(EstadoPelicula estadoPelicula);

    /**
     * - Consulta para obtener los horarios y salas de una pelicula
     * @param atributo: codigo de la pelicula, codigo del teatro
     * @return lista del detalle que tiene: pelicula, horario, sala
     */
    @Query("select new " + direccion + ".DetallePeliculaHorarioDTO( p, f.horario, f.sala ) from Pelicula p join p.funciones f where p.codigo = :codigoPelicula and f.sala.teatro.codigo = :codigoTeatro")
    List<DetallePeliculaHorarioDTO> peliculaHorariosSalas(Integer codigoPelicula, Integer codigoTeatro);

    /**
     * Consulta para obtener la pelicula mas vista de una ciudad
     * @param atributo: codigo de la ciudad
     * @return lista de elementos con nombre de la pelicula y cantidad de funciones
     */
    @Query("select p.nombre, count(c) from Funcion f join f.compras c join f.pelicula p where f.sala.teatro.ciudad.codigo = :codigoCiudad group by p.nombre order by count(c) desc")
    List<Object[]> peliculaTaquilleraCiudad(Integer codigoCiudad);
}
