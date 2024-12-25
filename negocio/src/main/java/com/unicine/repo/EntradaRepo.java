package com.unicine.repo;

import com.unicine.dto.DetalleSillaDTO;
import com.unicine.entidades.Entrada;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EntradaRepo extends JpaRepository<Entrada, Integer> {
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    // REVIEW: La razón de esta variable es para evitar escribir el nombre completo de la clase en la consulta es inutil para una sola consulta para para varios DTO es util
    String direccion = "com.unicine.dto";

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
}
