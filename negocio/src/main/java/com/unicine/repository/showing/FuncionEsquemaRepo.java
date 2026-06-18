package com.unicine.repository.showing;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unicine.entity.showing.FuncionEsquema;

@Repository
public interface FuncionEsquemaRepo extends JpaRepository<FuncionEsquema, Integer> {

// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    /**
     * Consulta para obtener el esquema de una funcion
     * @param codigoFuncion codigo de la funcion
     * @return esquema de la funcion
     */
    Optional<FuncionEsquema> findByFuncionCodigo(Integer codigoFuncion);
}
