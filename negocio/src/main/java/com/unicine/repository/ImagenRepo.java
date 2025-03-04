package com.unicine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unicine.entity.Imagen;

@Repository
public interface ImagenRepo extends JpaRepository<Imagen, String> {
    
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    @Query("select i from Imagen i where (i.cliente.cedula = :cedula) or (i.administrador.cedula = :cedula) or (i.administradorTeatro.cedula = :cedula)")
    Optional<Imagen> findByPersona(Integer cedula);
}
