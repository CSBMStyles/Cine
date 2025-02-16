package com.unicine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unicine.entity.CompraConfiteria;

@Repository
public interface CompraConfiteriaRepo extends JpaRepository<CompraConfiteria, Integer> {
    
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

}
