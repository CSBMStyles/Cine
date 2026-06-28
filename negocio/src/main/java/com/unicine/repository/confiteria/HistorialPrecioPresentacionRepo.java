package com.unicine.repository.confiteria;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unicine.entity.confiteria.HistorialPrecioPresentacion;

@Repository
public interface HistorialPrecioPresentacionRepo extends JpaRepository<HistorialPrecioPresentacion, Integer> {

    /**
     * Lista el historial de precios de una presentacion ordenado por fecha descendente.
     *
     * @param codigoPresentacion codigo de la presentacion
     * @return lista de historiales
     */
    List<HistorialPrecioPresentacion> findByPresentacionCodigoOrderByFechaCambioDesc(Integer codigoPresentacion);

    /**
     * Obtiene el ultimo historial de precios de una presentacion.
     *
     * @param codigoPresentacion codigo de la presentacion
     * @return ultimo historial, si existe
     */
    Optional<HistorialPrecioPresentacion> findTopByPresentacionCodigoOrderByFechaCambioDesc(Integer codigoPresentacion);
}
