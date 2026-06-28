package com.unicine.test.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.confiteria.ConfiteriaPresentacion;
import com.unicine.entity.purchase.Compra;
import com.unicine.entity.purchase.CompraConfiteria;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.confiteria.ConfiteriaPresentacionRepo;
import com.unicine.repository.purchase.CompraConfiteriaRepo;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.service.purchase.CompraConfiteriaServicio;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CompraConfiteriaServicioTest {

    @Autowired
    private CompraConfiteriaServicio compraConfiteriaServicio;

    @Autowired
    private CompraConfiteriaRepo compraConfiteriaRepo;

    @Autowired
    private CompraRepo compraRepo;

    @Autowired
    private ConfiteriaPresentacionRepo presentacionRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() throws Exception {
        Compra compra = compraRepo.findById(1).orElse(null);
        ConfiteriaPresentacion presentacion = presentacionRepo.findById(3).orElse(null);

        CompraConfiteria item = CompraConfiteria.builder()
                .precio(28000.0)
                .unidades(1)
                .compra(compra)
                .presentacion(presentacion)
                .build();

        CompraConfiteria guardado = compraConfiteriaServicio.registrar(item);

        Assertions.assertNotNull(guardado);
        Assertions.assertEquals(28000.0, guardado.getPrecio());
        Assertions.assertEquals(1, guardado.getUnidades());

        System.out.println("\nRegistro guardado:");
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarCompraInexistente() {
        ConfiteriaPresentacion presentacion = presentacionRepo.findById(1).orElse(null);

        Compra compraInexistente = new Compra();
        compraInexistente.setCodigo(999);

        CompraConfiteria item = CompraConfiteria.builder()
                .precio(10000.0)
                .unidades(1)
                .compra(compraInexistente)
                .presentacion(presentacion)
                .build();

        Assertions.assertThrows(ResourceNotFoundException.class, () -> compraConfiteriaServicio.registrar(item));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarPresentacionInexistente() {
        Compra compra = compraRepo.findById(1).orElse(null);

        ConfiteriaPresentacion presentacionInexistente = new ConfiteriaPresentacion();
        presentacionInexistente.setCodigo(999);

        CompraConfiteria item = CompraConfiteria.builder()
                .precio(10000.0)
                .unidades(1)
                .compra(compra)
                .presentacion(presentacionInexistente)
                .build();

        Assertions.assertThrows(ResourceNotFoundException.class, () -> compraConfiteriaServicio.registrar(item));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() throws Exception {
        CompraConfiteria buscado = compraConfiteriaServicio.obtener(1).orElse(null);

        buscado.setUnidades(5);
        buscado.setPrecio(7500.0);

        CompraConfiteria actualizado = compraConfiteriaServicio.actualizar(buscado);

        Assertions.assertEquals(5, actualizado.getUnidades());
        Assertions.assertEquals(7500.0, actualizado.getPrecio());

        System.out.println("\nRegistro actualizado:");
        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() throws Exception {
        CompraConfiteria buscado = compraConfiteriaServicio.obtener(1).orElse(null);

        compraConfiteriaServicio.eliminar(buscado, true);

        Optional<CompraConfiteria> verificacion = compraConfiteriaRepo.findById(1);

        Assertions.assertTrue(verificacion.isEmpty());

        System.out.println("\nRegistro eliminado");
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminarSinConfirmacion() throws Exception {
        CompraConfiteria buscado = compraConfiteriaServicio.obtener(1).orElse(null);

        Assertions.assertThrows(BusinessRuleException.class, () -> compraConfiteriaServicio.eliminar(buscado, false));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() throws Exception {
        Optional<CompraConfiteria> buscado = compraConfiteriaServicio.obtener(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\nRegistro obtenido:");
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {
        List<CompraConfiteria> items = compraConfiteriaServicio.listar();

        Assertions.assertEquals(8, items.size());

        System.out.println("\nListado de registros:");
        items.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {
        List<CompraConfiteria> items = compraConfiteriaServicio.listarPaginado();

        Assertions.assertEquals(8, items.size());

        System.out.println("\nListado paginado:");
        items.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorCompra() throws Exception {
        List<CompraConfiteria> items = compraConfiteriaServicio.listarPorCompra(1);

        Assertions.assertEquals(2, items.size());

        System.out.println("\nItems de confiteria por compra:");
        items.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorPresentacion() throws Exception {
        List<CompraConfiteria> items = compraConfiteriaServicio.listarPorPresentacion(1);

        Assertions.assertEquals(2, items.size());

        System.out.println("\nItems de la presentacion en compras:");
        items.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void calcularTotalPorCompra() throws Exception {
        Double total = compraConfiteriaServicio.calcularTotalPorCompra(1);

        Assertions.assertEquals(27000.0, total);

        System.out.println("\nTotal de confiteria por compra: " + total);
    }
}
