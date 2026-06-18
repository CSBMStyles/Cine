package com.unicine.repository.purchase;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unicine.entity.purchase.Cupon;

@Repository
public interface CuponRepo extends JpaRepository<Cupon, Integer> {

// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    /**
     * Lista los cupones cuya fecha de vencimiento es posterior a la fecha dada.
     *
     * @param fecha Fecha de referencia
     * @return Lista de cupones activos respecto a la fecha
     */
    List<Cupon> findByFechaVencimientoAfter(LocalDateTime fecha);

    /**
     * Lista los cupones cuya fecha de vencimiento es anterior a la fecha dada.
     *
     * @param fecha Fecha de referencia
     * @return Lista de cupones vencidos respecto a la fecha
     */
    List<Cupon> findByFechaVencimientoBefore(LocalDateTime fecha);

    /**
     * Busca cupones cuyo criterio contenga el texto dado (ignora mayusculas/minusculas).
     *
     * @param criterio Texto a buscar
     * @return Lista de cupones coincidentes
     */
    List<Cupon> findByCriterioContainingIgnoreCase(String criterio);

    /**
     * Lista los cupones cuyo descuento este dentro del rango indicado.
     *
     * @param min Valor minimo del rango
     * @param max Valor maximo del rango
     * @return Lista de cupones dentro del rango
     */
    List<Cupon> findByDescuentoBetween(Double min, Double max);

    /**
     * Lista los cupones que tienen al menos una asignacion a clientes.
     *
     * @return Lista de cupones con asignaciones
     */
    @Query("select c from Cupon c where size(c.cuponClientes) > 0")
    List<Cupon> findConAsignaciones();
}
