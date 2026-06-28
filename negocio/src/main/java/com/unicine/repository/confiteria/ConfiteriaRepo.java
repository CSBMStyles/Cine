package com.unicine.repository.confiteria;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unicine.entity.confiteria.Confiteria;
import com.unicine.enums.confiteria.CategoriaConfiteria;

@Repository
public interface ConfiteriaRepo extends JpaRepository<Confiteria, Integer> {

    // NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    /**
     * Lista los productos filtrados por categoria.
     *
     * @param categoria Categoria de producto
     * @return Lista de productos de esa categoria
     */
    List<Confiteria> findByCategoria(CategoriaConfiteria categoria);

    /**
     * Busca productos cuyo nombre contenga el texto dado (ignora mayusculas/minusculas).
     *
     * @param nombre Texto a buscar
     * @return Lista de productos coincidentes
     */
    @Query("select c from Confiteria c where lower(c.nombre) like lower(concat('%', :nombre, '%'))")
    List<Confiteria> buscarPorNombre(String nombre);
}
