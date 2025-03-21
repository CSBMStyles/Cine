package com.unicine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unicine.entity.Administrador;

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
     * Consulta para obtener un administrador por su correo excluyendo el administrador que esta actualmente
     * @param correo
     * @return administrador
     */
    @Query("select a from Administrador a where a.correo = :correo and a.cedula != :cedula")
    Optional<Administrador> buscarCorreoExcluido(String correo, Integer cedula);

    /**
     * Consulta para comprobar la autenticacion de un administrador
     * @param atributos: correo y password del administrador
     * @return administrador
     */
    @Query("select a from Administrador a where a.correo = :correo and a.password = :password")
    Optional<Administrador> comprobarAutenticacion(String correo, String password);
}
