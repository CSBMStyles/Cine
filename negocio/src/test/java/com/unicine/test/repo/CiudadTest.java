package com.unicine.test.repo;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import com.unicine.entidades.Ciudad;
import com.unicine.repo.CiudadRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CiudadTest {

    /**
     * NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado
     */

    @Autowired
    private CiudadRepo ciudadRepo;

    // SECTION: Consultas basicas para la base de datos
    
    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Ciudad ciudad = new Ciudad("Cartagena");
        ciudad.setCodigo(6);

        Ciudad guardado = ciudadRepo.save(ciudad);

        Assertions.assertEquals("Cartagena", guardado.getNombre());

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Ciudad guardado = ciudadRepo.findById(1).orElse(null);

        System.out.println(guardado);

        guardado.setNombre("Cucuta");

        Ciudad actualizado = ciudadRepo.save(guardado);

        Assertions.assertEquals("Cucuta", actualizado.getNombre());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Ciudad buscado = ciudadRepo.findById(1).orElse(null);

        System.out.println(buscado);

        ciudadRepo.delete(buscado);

        Ciudad verificacion = ciudadRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<Ciudad> buscado = ciudadRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<Ciudad> ciudades = ciudadRepo.findAll();

        Assertions.assertEquals(5, ciudades.size());

        System.out.println("\n" + "Listado de registros:");

        for (Ciudad c : ciudades) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Ciudad> ciudades = ciudadRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, ciudades.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (Ciudad c : ciudades) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<Ciudad> ciudades = ciudadRepo.findAll(Sort.by("nombre"));

        Assertions.assertEquals(5, ciudades.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (Ciudad c : ciudades) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void contarTeatrosCiudad() {

        List<Object[]> ciudades = ciudadRepo.contarTeatrosCiudad();

        Assertions.assertNotNull(ciudades);

        System.out.println("\n" + "Cantidad de teatros en cada ciudad:");

        for (Object[] c : ciudades) {
            System.out.println(c[0] + " - " + c[1]);
        }
    }
}
