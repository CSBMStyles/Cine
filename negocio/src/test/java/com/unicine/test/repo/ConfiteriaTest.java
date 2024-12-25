package com.unicine.test.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import com.unicine.entidades.Confiteria;
import com.unicine.repo.ConfiteriaRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ConfiteriaTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado */

    @Autowired
    private ConfiteriaRepo confiteriaRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        // Creamos un mapa de imágenes
        Map<String, String> imagenes = new HashMap<>();
        imagenes.put("http://example.com/imagen-1.jpg", "perfil");

        Confiteria confiteria = new Confiteria("Papas Fritas", 5000.00, imagenes);
        confiteria.setCodigo(6);

        Confiteria guardado = confiteriaRepo.save(confiteria);

        Assertions.assertEquals("Papas Fritas", guardado.getNombre());

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Confiteria guardado = confiteriaRepo.findById(1).orElse(null);

        System.out.println(guardado);

        guardado.setNombre("Combo para Adultos");

        Confiteria actualizado = confiteriaRepo.save(guardado);

        Assertions.assertEquals("Combo para Adultos", actualizado.getNombre());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Confiteria buscado = confiteriaRepo.findById(1).orElse(null);

        System.out.println(buscado);

        confiteriaRepo.delete(buscado);

        Confiteria verificacion = confiteriaRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<Confiteria> buscado = confiteriaRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<Confiteria> confiterias = confiteriaRepo.findAll();

        Assertions.assertEquals(5, confiterias.size());

        System.out.println("\n" + "Listado de registros:");

        for (Confiteria c : confiterias) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Confiteria> confiterias = confiteriaRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, confiterias.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (Confiteria c : confiterias) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<Confiteria> confiterias = confiteriaRepo.findAll(Sort.by("nombre"));

        Assertions.assertEquals(5, confiterias.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (Confiteria c : confiterias) {
            System.out.println(c);
        }
    }
}
