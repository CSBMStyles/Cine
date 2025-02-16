package com.unicine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unicine.entity.Coleccion;
import com.unicine.entity.ColeccionComposicion;

@Repository
public interface ColeccionRepo extends JpaRepository<Coleccion, ColeccionComposicion> {
    
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

}
