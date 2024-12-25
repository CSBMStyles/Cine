package com.unicine.repo;

import com.unicine.entidades.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CuponClienteRepo extends JpaRepository<CuponCliente, Integer> {
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    // SECTION: Relacion propia

    /**
     * Consulta para obtener un cupon de un cliente
     * @param atributos: codigo del cupon, cedula del cliente
     * @return cupon
     */
    @Query("select c from CuponCliente c where c.cupon.codigo = :codigoCupon and c.cliente.cedula = :cedula")
    Optional<CuponCliente> obtenerCuponCliente(Integer codigoCupon, Integer cedula);

    /**
     * Consulta para listar los cupones de un cliente
     * @param atributos: cedula del cliente
     * @return lista de cupones
     */
    @Query("select c from CuponCliente c where c.cliente.cedula = :cedula")
    List<CuponCliente> listarCuponesCliente(Integer cedula);

    // SECTION: Relacion con compra

    /**
     * #️⃣ Consulta para contar los cupones redimidos por cliente
     * @return lista de cupones redimidos por cliente
     */
    @Query("select c.cliente.cedula, c.cliente.nombre, count(c) from Compra c where c.cuponCliente is not null group by c.cliente.cedula")
    List<Object[]> contarCuponesRedimidosCliente();
}
