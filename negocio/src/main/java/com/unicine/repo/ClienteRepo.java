package com.unicine.repo;

// REVIEW: Recordar modificar entidades especificas para que se ajusten a las necesidades del proyecto
import com.unicine.entidades.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ClienteRepo extends JpaRepository<Cliente, Integer> {
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    // NOTE: Se crea un metodo para buscar un cliente por su correo la razon por la que no se usa el @Querry es porque se puede hacer una inferencia de la consulta usando el nombre del metodo y el nombre de la columna
    
    /**
     * Consulta para obtener un cliente por su correo
     * @param atributo: correo del cliente
     * @return cliente
     */
    Optional<Cliente> findByCorreo(String correo);

    /**
     * Consulta para obtener un cliente por su estado
     * @param atributo: estado del cliente
     * @return lista de clientes
    */
    List<Cliente> findByEstado(Boolean estado);

    // NOTE: Una forma de recibir parametros en una consulta es usando el signo de interrogacion y el numero de la posicion del parametro

    /**
     * Consulta para comprobar la autenticacion de un cliente
     * @param atributos: correo del cliente, password del cliente
     * @return cliente
     */
    @Query("select c from Cliente c where c.correo = ?1 and c.password = ?2")
    Optional<Cliente> comprobarAutenticacion(String correo, String password);
}
