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

import com.unicine.entidades.Compra;
import com.unicine.entidades.CompraConfiteria;
import com.unicine.entidades.Confiteria;
import com.unicine.repo.CompraConfiteriaRepo;
import com.unicine.repo.CompraRepo;
import com.unicine.repo.ConfiteriaRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CompraConfiteriaTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado */

    @Autowired
    private CompraConfiteriaRepo compraConfiteriaRepo;

    @Autowired
    private CompraRepo compraRepo;

    @Autowired
    private ConfiteriaRepo confiteriaRepo;

    // SECTION: Consultas basicas para la base de datos

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Compra compra = compraRepo.findById(1).orElse(null);

        Confiteria confiteria = confiteriaRepo.findById(1).orElse(null);

        CompraConfiteria compraConfiteria = new CompraConfiteria(16000.00, 2, compra, confiteria);
        compraConfiteria.setCodigo(7);

        CompraConfiteria guardado = compraConfiteriaRepo.save(compraConfiteria);

        Assertions.assertNotNull(guardado);

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        CompraConfiteria guardado = compraConfiteriaRepo.findById(1).orElse(null);

        System.out.println(guardado);

        guardado.setUnidades(1);

        CompraConfiteria actualizado = compraConfiteriaRepo.save(guardado);

        Assertions.assertEquals(1, actualizado.getUnidades());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        CompraConfiteria buscado = compraConfiteriaRepo.findById(1).orElse(null);

        System.out.println(buscado);

        compraConfiteriaRepo.delete(buscado);

        CompraConfiteria verificacion = compraConfiteriaRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<CompraConfiteria> buscado = compraConfiteriaRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<CompraConfiteria> comprasConfiterias = compraConfiteriaRepo.findAll();

        Assertions.assertEquals(6, comprasConfiterias.size());

        System.out.println("\n" + "Listado de registros:");

        for (CompraConfiteria c : comprasConfiterias) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<CompraConfiteria> comprasConfiterias = compraConfiteriaRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, comprasConfiterias.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (CompraConfiteria c : comprasConfiterias) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<CompraConfiteria> comprasConfiterias = compraConfiteriaRepo.findAll(Sort.by("codigo"));

        Assertions.assertEquals(6, comprasConfiterias.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (CompraConfiteria c : comprasConfiterias) {
            System.out.println(c);
        }
    }
}
