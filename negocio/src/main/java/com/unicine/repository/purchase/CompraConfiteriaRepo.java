package com.unicine.repository.purchase;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unicine.entity.purchase.CompraConfiteria;

@Repository
public interface CompraConfiteriaRepo extends JpaRepository<CompraConfiteria, Integer> {

// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    // SECTION: Relacion con compra

    /**
     * Consulta para obtener los items de confiteria de una compra.
     * @param codigoCompra codigo de la compra
     * @return lista de items de confiteria
     */
    List<CompraConfiteria> findByCompraCodigo(Integer codigoCompra);

    /**
     * Calcula el total de confiteria de una compra sumando precio * unidades.
     * @param codigoCompra codigo de la compra
     * @return total de confiteria
     */
    @Query("select coalesce(sum(cc.precio * cc.unidades), 0) from CompraConfiteria cc where cc.compra.codigo = :codigoCompra")
    Double calcularTotalPorCompra(Integer codigoCompra);

    // SECTION: Relacion con confiteria

    /**
     * Consulta para obtener los items de una confiteria en todas las compras.
     * @param codigoConfiteria codigo de la confiteria
     * @return lista de items de confiteria
     */
    List<CompraConfiteria> findByConfiteriaCodigo(Integer codigoConfiteria);
}
