package com.unicine.repository.purchase;

import com.unicine.entity.purchase.Entrada;
import com.unicine.transfer.data.DetalleSillaDTO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EntradaRepo extends JpaRepository<Entrada, Integer> {
    
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    // REVIEW: La razón de esta variable es para evitar escribir el nombre completo de la clase en la consulta es inutil para una sola consulta para para varios DTO es util
    String direccion = "com.unicine.transfer.data";

    // SECTION: Relacion con compra

    /**
     * Consulta para obtener las entradas de una compra
     * @param atributos: codigo de la compra
     * @return lista de entradas
     */
    @Query("select e from Compra c join c.entradas e where c.codigo = :codigoCompra")
    List<Entrada> obtenerEntradasCompra(Integer codigoCompra);

    // SECTION: Relacion con funcion

    /**
     * Consulta para obtener las sillas ocupadas de una funcion
     * @param atributos: codigo de la funcion
     * @return codigo, fila y columna de las entradas
     */
    @Query("select new " + direccion + ".DetalleSillaDTO(e.codigo, e.fila, e.columna ) from Compra comp join comp.entradas e join comp.funcion f where f.codigo = :codigoFuncion")
    List<DetalleSillaDTO> obtenerSillasOcupadas(Integer codigoFuncion);

    /**
     * Consulta para verificar si una silla especifica ya esta ocupada
     * en una funcion determinada.
     * Util para validar disponibilidad antes de registrar una entrada.
     * @param fila fila de la silla
     * @param columna columna de la silla
     * @param codigoFuncion codigo de la funcion
     * @return true si la silla esta ocupada, false en caso contrario
     */
    @Query("select count(e) > 0 from Entrada e where e.fila = :fila and e.columna = :columna and e.compra.funcion.codigo = :codigoFuncion")
    Boolean sillaOcupadaFuncion(Integer fila, Integer columna, Integer codigoFuncion);
}
