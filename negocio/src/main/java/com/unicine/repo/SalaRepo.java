package com.unicine.repo;

import com.unicine.entidades.Sala;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaRepo extends JpaRepository<Sala, Integer> {
    
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    /**
     * Consulta para obtener una salas por su nombre
     * @param atributo: nombre de la sala
     * @return Sala
     */
    @Query("select s from Sala s where s.nombre like concat('%',:nombre,'%')")
    List<Sala> buscarNombre(String nombre);

    /**
     * Consulta para obtener una sala por su nombre de un teatro
     * @param nombre
     * @param teatro
     * @return Sala
     */
    @Query("select s from Sala s where s.nombre = :nombreSala and s.teatro.codigo = :codigoTeatro")
    Optional<Sala> buscarNombreValidacion(String nombreSala, Integer codigoTeatro);

    /**
     * Consulta para obtener una sala por su nombre de un teatro excluyendo la sala actual de consulta
     * @param nombre
     * @param teatro
     * @param codigo
     * @return Sala
     */
    @Query("select s from Sala s where s.nombre = :nombreSala and s.teatro.codigo = :codigoTeatro and s.codigo != :codigoSala")
    Optional<Sala> buscarNombreExcluido(String nombreSala, Integer codigoTeatro, Integer codigoSala);

    /**
     * Consulta para obtener un salas apatir de su nombre del teatro que pertenece la sala
     * @param atributo: nombre de la sala
     * @return Sala
     */
    @Query("select s from Sala s where s.nombre like concat('%',:nombreSala,'%') and s.teatro.codigo = :codigoTeatro")
    List<Sala> buscarNombreTeatro(String nombreSala, Integer codigoTeatro);

    /**
     * Consulta para obtener una sala apartir de su codigo del teatro al que pertenece
     * @param atributo: nombre de la sala, codigo del teatro
     * @return Sala
     */
    @Query("select s from Sala s where s.codigo = :codigoSala and s.teatro.codigo = :codigoTeatro")
    Optional<Sala> buscarIdTeatro(Integer codigoSala, Integer codigoTeatro);
}
