package com.unicine.repo;

import com.unicine.entidades.DistribucionSilla;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DistribucionSillaRepo extends JpaRepository<DistribucionSilla, Integer> {
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    // SECTION: Relacion con funcion

    /**
     * Metodo que permite mostrar la distribucion de las sillas de una funcion
     * @param atributo: codigo de la funcion
     * @return distribucion de silla
     */
    @Query("select ds from Sala s join s.distribucionSilla ds join s.funciones f where f.codigo = :codigoFuncion")
    Optional<DistribucionSilla> obtenerDistribucionFuncion(Integer codigoFuncion);
}
