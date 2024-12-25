package com.unicine.repo;

import com.unicine.entidades.Administrador;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepo extends JpaRepository<Administrador, Integer> {
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    /**
     * Consulta para obtener un administrador por su correo
     * @param atributo: correo del administrador
     * @return administrador
     */
    Optional<Administrador> findByCorreo(String correo);

    /**
     * Consulta para comprobar la autenticacion de un administrador
     * @param atributos: correo y password del administrador
     * @return administrador
     */
    @Query("select a from Administrador a where a.correo = :correo and a.password = :password")
    Optional<Administrador> comprobarAutenticacion(String correo, String password);
}
