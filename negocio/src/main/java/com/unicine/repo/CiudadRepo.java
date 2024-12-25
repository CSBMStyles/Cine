package com.unicine.repo;

import com.unicine.entidades.Ciudad;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiudadRepo extends JpaRepository<Ciudad, Integer> {
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    /**
     * Consulta para obtener una ciudad por su nombre
     * @param atributo: nombre de la ciudad
     * @return ciudad
     */
    @Query("select c from Ciudad c where c.nombre like concat('%',:nombre,'%')")
    List<Ciudad> findByNombre(String nombre);

    /**
     * #️⃣ Consulta para contar el número de teatros que hay por cada ciudad.
     * @return ciudad, conteo
     */
    @Query("select c.nombre, count(t) from Ciudad c join c.teatros t group by c.nombre")
    List<Object[]> contarTeatrosCiudad();
}
