package com.unicine.repo;

import com.unicine.entidades.Teatro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeatroRepo extends JpaRepository<Teatro, Integer> {
    
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    /**
     * Consulta para obtener un teatro por su direccion de una ciudad
     * @param atributo: direccion del cliente
     * @return teatro
     */
    @Query("select t from Teatro t where t.direccion = :direccion and t.ciudad.codigo = :codigoCiudad")
    Optional<Teatro> findByDireccion(String direccion, Integer codigoCiudad);

    /**
     * Consulta para obtener un teatro por su direccion de una ciudad excluyendo un teatro
     * @param atributo: direccion del cliente
     * @return teatro
     */
    @Query("select t from Teatro t where t.direccion = :direccion and t.ciudad.codigo = :codigoCiudad and t.codigo != :codigoTeatro")
    Optional<Teatro> buscarDireccionExcluido(String direccion, Integer codigoCiudad, Integer codigoTeatro);

    /**
     * Consulta para obtener los teatros de una ciudad
     * @param atributo: nombre de la ciudad
     * @return lista de teatros
     */
    @Query("select t from Teatro t where t.ciudad.nombre = :nombreCiudad")
    List<Teatro> listarTeatrosCiudad(String nombreCiudad);
}
