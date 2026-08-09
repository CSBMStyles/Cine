package com.unicine.repository.purchase;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unicine.entity.purchase.CuponCliente;

@Repository
public interface CuponClienteRepo extends JpaRepository<CuponCliente, Integer> {

// Note: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    // SECTION: Relacion propia

    /**
     * Obtiene la asignacion de un cupon a un cliente especifico.
     *
     * @param codigoCupon Codigo del cupon
     * @param cedula Cedula del cliente
     * @return Asignacion del cupon al cliente
     */
    Optional<CuponCliente> findByCuponCodigoAndClienteCedula(Integer codigoCupon, Integer cedula);

    /**
     * Lista las asignaciones de cupones de un cliente.
     *
     * @param cedula Cedula del cliente
     * @return Lista de asignaciones del cliente
     */
    List<CuponCliente> findByClienteCedula(Integer cedula);

    /**
     * Lista las asignaciones activas de un cliente.
     *
     * @param cedula Cedula del cliente
     * @param estado Estado de la asignacion
     * @return Lista de asignaciones activas del cliente
     */
    List<CuponCliente> findByClienteCedulaAndEstado(Integer cedula, Boolean estado);

    // !SECTION
    // SECTION: Relacion con compra

    /**
     * Cuenta los cupones redimidos agrupados por cliente.
     *
     * @return Lista con cedula, nombre y cantidad de cupones redimidos
     */
    @Query("select c.cliente.cedula, c.cliente.nombre, count(c) from Compra c where c.cuponCliente is not null group by c.cliente.cedula")
    List<Object[]> contarCuponesRedimidosCliente();

    /**
     * Cuenta los cupones redimidos por un cliente especifico.
     *
     * @param cedula Cedula del cliente
     * @return Cantidad de cupones redimidos por el cliente
     */
    @Query("select count(c) from Compra c where c.cuponCliente.cliente.cedula = :cedula and c.cuponCliente is not null")
    Long contarRedimidosPorCliente(Integer cedula);
    // !SECTION
}
