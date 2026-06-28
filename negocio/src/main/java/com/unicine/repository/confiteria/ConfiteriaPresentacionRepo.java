package com.unicine.repository.confiteria;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unicine.entity.confiteria.ConfiteriaPresentacion;

@Repository
public interface ConfiteriaPresentacionRepo extends JpaRepository<ConfiteriaPresentacion, Integer> {

    /**
     * Lista todas las presentaciones de una confiteria.
     *
     * @param codigoConfiteria codigo de la confiteria
     * @return lista de presentaciones
     */
    List<ConfiteriaPresentacion> findByConfiteriaCodigo(Integer codigoConfiteria);

    /**
     * Busca presentaciones cuyo precio actual sea menor que el precio base,
     * es decir, que tengan un descuento temporal activo.
     *
     * @return lista de presentaciones con precio temporal
     */
    @Query("select p from ConfiteriaPresentacion p where p.precio < p.precioBase")
    List<ConfiteriaPresentacion> findConDescuentoTemporalActivo();

    /**
     * Busca presentaciones con descuento temporal y fecha de expiracion ya vencida.
     *
     * @param ahora fecha/hora de referencia
     * @return lista de presentaciones a revertir
     */
    @Query("select p from ConfiteriaPresentacion p where p.precio < p.precioBase and p.fechaExpiracionTemporal is not null and p.fechaExpiracionTemporal <= :ahora")
    List<ConfiteriaPresentacion> findPresentacionesConDescuentoVencido(java.time.LocalDateTime ahora);
}
