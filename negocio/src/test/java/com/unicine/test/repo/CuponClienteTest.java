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

import com.unicine.entidades.Cliente;
import com.unicine.entidades.Cupon;
import com.unicine.entidades.CuponCliente;
import com.unicine.repo.ClienteRepo;
import com.unicine.repo.CuponClienteRepo;
import com.unicine.repo.CuponRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CuponClienteTest {

    /*
     * NOTE: En las pruebas de unitarias o de integracion se menciona que se debe
     * comprobar el resultado con el Assertions, pero no esta de mas imprimir el
     * resultado para verificar visualmente que se esta obteniendo lo esperado
     */

    @Autowired
    private CuponClienteRepo cuponClienteRepo;

    @Autowired
    private CuponRepo cuponRepo;

    @Autowired
    private ClienteRepo clienteRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Cupon cupon = cuponRepo.findById(1).orElse(null);

        Cliente cliente = clienteRepo.findById(1009000011).orElse(null);

        CuponCliente cuponCliente = new CuponCliente(false, cupon, cliente);
        cuponCliente.setCodigo(6);

        CuponCliente guardado = cuponClienteRepo.save(cuponCliente);

        Assertions.assertNotNull(guardado);

        System.out.println("\n" + "Registro guardado:");

        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        CuponCliente guardado = cuponClienteRepo.findById(1).orElse(null);

        System.out.println(guardado);

        guardado.setEstado(false);

        CuponCliente actualizado = cuponClienteRepo.save(guardado);

        Assertions.assertEquals(false, actualizado.getEstado());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        CuponCliente buscado = cuponClienteRepo.findById(1).orElse(null);

        System.out.println(buscado);

        cuponClienteRepo.delete(buscado);

        CuponCliente verificacion = cuponClienteRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<CuponCliente> buscado = cuponClienteRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");

        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<CuponCliente> cuponesClientes = cuponClienteRepo.findAll();

        Assertions.assertEquals(5, cuponesClientes.size());

        System.out.println("\n" + "Listado de registros:");

        for (CuponCliente c : cuponesClientes) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<CuponCliente> cuponesClientes = cuponClienteRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, cuponesClientes.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (CuponCliente c : cuponesClientes) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<CuponCliente> cuponesClientes = cuponClienteRepo.findAll(Sort.by("codigo"));

        Assertions.assertEquals(5, cuponesClientes.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (CuponCliente c : cuponesClientes) {
            System.out.println(c);
        }
    }

    //SECTION: Consultas personalizadas para la base de datos

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerCuponCliente() {
        
        Optional<CuponCliente> cuponesClientes = cuponClienteRepo.obtenerCuponCliente(1, 1005000055);

        Assertions.assertTrue(cuponesClientes.isPresent());

        System.out.println("\n" + "Listado de registros obtenidos por cliente:");

        System.out.println(cuponesClientes.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarCuponesCliente() {
        
        List<CuponCliente> cuponesClientes = cuponClienteRepo.listarCuponesCliente(1006000044);

        Assertions.assertEquals(2, cuponesClientes.size());

        System.out.println("\n" + "Listado de registros obtenidos por cliente:");

        for (CuponCliente c : cuponesClientes) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void contarCuponesRedimidosCliente() {
        
        List<Object[]> cuponesClientes = cuponClienteRepo.contarCuponesRedimidosCliente();

        Assertions.assertNotNull(cuponesClientes);

        System.out.println("\n" + "Listado de registros obtenidos por cliente:");

        for (Object[] c : cuponesClientes) {
            System.out.println("Cedula: " + c[0] + " Nombre: " + c[1] + " Cantidad: " + c[2]);
        }
    }
}
