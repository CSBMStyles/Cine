package com.unicine.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unicine.entity.Horario;

@Repository
public interface HorarioRepo extends JpaRepository<Horario, Integer> {
    
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    /**
     * Consulta para obtener un horario por su fecha
     * @param atributo: fecha del horario
     * @return horario
     */
    Optional<Horario> findByFechaInicio(LocalDate fecha);
}
