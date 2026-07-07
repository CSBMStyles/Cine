package com.unicine.repository.purchase;

import com.unicine.entity.purchase.Compra;
import com.unicine.enums.purchase.MedioPago;
import com.unicine.transfer.dto.response.DetalleCompraResponse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CompraRepo extends JpaRepository<Compra, Integer> {
    
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    // REVIEW: La razón de esta variable es para evitar escribir el nombre completo de la clase en la consulta es inutil para una sola consulta para para varios DTO es util
    String direccion = "com.unicine.transfer.dto.response";

    // NOTE: En el caso de necesitar conexiones para hacer la consulta como esta donde necesatamos obtener la compras del cliente se puede hacer con join se puede usar en lugar del in, ambos cumplen la misma función, es así como la cláusula anterior

    // SECTION: Relacion con cliente, compra

    /**
     * Consulta para obtener las compras de un cliente
     * @param atributo: cedula del cliente
     * @return lista de compras
     */
    @Query("select c from Cliente cl join cl.compras c where cl.cedula = :cedula")
    List<Compra> obtenerComprasCedula(Integer cedula);
    
    // NOTE: Tambien se puede usar in para hacer la conexion de las tablas para la consulta, pero el in es mas usado para para poder acceder a los elementos de los atributos de tipo List. Ejemplo, si se desea obtener todos los departamentos de Colombia, para identificar su uso debemos fijarnos en la relación entre las clases si va de uno a muchos en las dos formas

    /**
     * Consulta para obtener las compras de un cliente
     * @param atributo: correo del cliente
     * @return lista de compras
     */
    @Query("select compra from Cliente cliente, in(cliente.compras) compra where cliente.correo = :correo")
    List<Compra> obtenerComprasCorreo(String correo);

    // IMPORTANT: Las consultas no solo se limitan a un elemento, se puede hacer consulta de varios objetos o atributos de la entidad, entonces es necesario cambiar el tipo de retorno de la consulta a Object[], pero a la larga se recomendaría usar DTO(Data Transfer Object) para que sea mas entendible

    /**
     * Consulta para obtener las compras de los clientes
     * @return lista de compras y nombre del cliente
     */
    @Query("select c, cl.nombre from Cliente cl left join cl.compras c")
    List<Object[]> obtenerComprasClientes();

    /**
     * #️⃣ Consulta para obtener la información de las compras de un cliente
     * @param atributo: cedula del cliente
     * @return lista del detalle de la compra: valor total, fecha de compra, codigo de la funcion, total de las entradas, total de la confiteria
     */
    @Query("select new " + direccion + ".DetalleCompraResponse( c.valorTotal, c.fechaCompra, c.funcion.codigo, (select coalesce(sum(e.precio), 0) from Entrada e where e.compra.codigo = c.codigo), (select coalesce(sum(conf.precio * conf.unidades), 0) from CompraConfiteria conf where conf.compra.codigo = c.codigo) ) from Compra c where c.cliente.cedula = :cedulaCliente")
    List<DetalleCompraResponse> obtenerInformacionCompra(Integer cedulaCliente);

    // SECTION: Relacion con entrada

    /**
     * Consulta para obtener los precios de las entradas de una compra
     * @param atributo: codigo de la compra
     * @return lista de precios de las entradas
     */
    @Query("select e.precio from Entrada e where e.compra.codigo = :codigoCompra")
    List<Double> obtenerPreciosEntradaCompra(Integer codigoCompra);

    // SECTION: Relacion con confiteria

    /**
     * Consulta para obtener los precios de la confiteria de una compra
     * @param atributo: codigo de la compra
     * @return lista de precios de la confiteria
     */
    @Query("select conf.precio * conf.unidades from CompraConfiteria conf where conf.compra.codigo = :codigoCompra")
    List<Double> obtenerPreciosConfiteriaCompra(Integer codigoCompra);

    // SECTION: Relacion propia

    /**
     * #️⃣ Consulta para obtener el valor total de las compras de un cliente
     * @param atributo: cedula del cliente
     * @return valor total de las compras
     */
    @Query("select sum(c.valorTotal) from Cliente cl join cl.compras c where cl.cedula = :cedula")
    Double obtenerTotalCompras(Integer cedula);

    /**
     * #️⃣ Consulta para obtener la compra mas costosa de todods los clientes
     * @return lista de compras mas costosas
     */
    @Query("select cl.correo, c from Cliente cl join cl.compras c where c.valorTotal = (select max(c.valorTotal) from Compra c)")
    List<Object[]> obtenerCompraCostosa();

    // SECTION: Consultas adicionales de negocio

    /**
     * Consulta para obtener las compras asociadas a una funcion especifica.
     * Util para reportes de ocupacion y ventas por funcion.
     * @param codigoFuncion codigo de la funcion
     * @return lista de compras de la funcion
     */
    @Query("select c from Compra c where c.funcion.codigo = :codigoFuncion")
    List<Compra> obtenerComprasFuncion(Integer codigoFuncion);

    /**
     * Consulta para obtener las compras realizadas dentro de un rango de fechas.
     * Util para reportes de ventas por periodo.
     * @param inicio fecha inicial del rango
     * @param fin fecha final del rango
     * @return lista de compras en el rango
     */
    @Query("select c from Compra c where c.fechaCompra between :inicio and :fin")
    List<Compra> obtenerComprasRangoFecha(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Consulta para obtener las compras filtradas por medio de pago.
     * Util para reportes financieros por metodo de pago.
     * @param medioPago medio de pago utilizado
     * @return lista de compras con ese medio de pago
     */
    @Query("select c from Compra c where c.medioPago = :medioPago")
    List<Compra> obtenerComprasMedioPago(MedioPago medioPago);

    /**
     * Consulta para contar cuantas compras ha realizado un cliente.
     * Util para metricas de fidelizacion.
     * @param cedula cedula del cliente
     * @return cantidad de compras del cliente
     */
    @Query("select count(c) from Compra c where c.cliente.cedula = :cedula")
    Long contarComprasCliente(Integer cedula);

    /**
     * Consulta para obtener las compras filtradas por estado (procesadas o no).
     * @param estado estado de la compra
     * @return lista de compras con el estado indicado
     */
    @Query("select c from Compra c where c.estado = :estado")
    List<Compra> obtenerComprasEstado(Boolean estado);
}
