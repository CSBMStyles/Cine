package com.unicine.repo;

import com.unicine.entidades.AdministradorTeatro;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorTeatroRepo extends JpaRepository<AdministradorTeatro, Integer> {
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    /**
     * Consulta para obtener un administrador de teatro por su correo
     * @param atributo: correo del administrador de teatro
     * @return administrador de teatro
     */
    Optional<AdministradorTeatro> findByCorreo(String correo);

    /**
     * Consulta para comprobar la autenticacion de un administrador de teatro
     * @param atributos: correo y password del administrador
     * @return administrador de teatro
     */
    @Query("select a from AdministradorTeatro a where a.correo = :correo and a.password = :password")
    Optional<AdministradorTeatro> comprobarAutenticacion(String correo, String password);
}
