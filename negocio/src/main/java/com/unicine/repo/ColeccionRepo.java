package com.unicine.repo;

import com.unicine.entidades.Coleccion;
import com.unicine.entidades.ColeccionComposicion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColeccionRepo extends JpaRepository<Coleccion, ColeccionComposicion> {
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

}
