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

import com.unicine.entity.purchase.CompraConfiteria;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.purchase.CompraConfiteriaRepo;
import com.unicine.service.purchase.CompraConfiteriaServicio;
import com.unicine.transfer.dto.request.CompraConfiteriaRequest;
import com.unicine.transfer.dto.response.CompraConfiteriaResponse;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CompraConfiteriaServicioTest {

    @Autowired
    private CompraConfiteriaServicio compraConfiteriaServicio;

    @Autowired
    private CompraConfiteriaRepo compraConfiteriaRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() throws Exception {
        CompraConfiteriaRequest request = CompraConfiteriaRequest.builder()
                .precio(28000.0)
                .unidades(1)
                .compraCodigo(1)
                .presentacionCodigo(3)
                .build();

        CompraConfiteriaResponse guardado = compraConfiteriaServicio.registrar(request);

        Assertions.assertNotNull(guardado);
        Assertions.assertEquals(28000.0, guardado.getPrecio());
        Assertions.assertEquals(1, guardado.getUnidades());

        System.out.println("\nRegistro guardado:");
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarCompraInexistente() {
        CompraConfiteriaRequest request = CompraConfiteriaRequest.builder()
                .precio(10000.0)
                .unidades(1)
                .compraCodigo(999)
                .presentacionCodigo(1)
                .build();

        Assertions.assertThrows(ResourceNotFoundException.class, () -> compraConfiteriaServicio.registrar(request));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarPresentacionInexistente() {
        CompraConfiteriaRequest request = CompraConfiteriaRequest.builder()
                .precio(10000.0)
                .unidades(1)
                .compraCodigo(1)
                .presentacionCodigo(999)
                .build();

        Assertions.assertThrows(ResourceNotFoundException.class, () -> compraConfiteriaServicio.registrar(request));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() throws Exception {
        CompraConfiteriaResponse buscado = compraConfiteriaServicio.obtener(1).orElse(null);
        Assertions.assertNotNull(buscado);

        CompraConfiteria entidad = compraConfiteriaRepo.findById(1).orElse(null);
        Assertions.assertNotNull(entidad);

        CompraConfiteriaRequest request = CompraConfiteriaRequest.builder()
                .codigo(buscado.getCodigo())
                .precio(7500.0)
                .unidades(5)
                .compraCodigo(entidad.getCompra().getCodigo())
                .presentacionCodigo(entidad.getPresentacion().getCodigo())
                .build();

        CompraConfiteriaResponse actualizado = compraConfiteriaServicio.actualizar(request);

        Assertions.assertEquals(5, actualizado.getUnidades());
        Assertions.assertEquals(7500.0, actualizado.getPrecio());

        System.out.println("\nRegistro actualizado:");
        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() throws Exception {
        compraConfiteriaServicio.eliminar(1, true);

        Optional<CompraConfiteria> verificacion = compraConfiteriaRepo.findById(1);

        Assertions.assertTrue(verificacion.isEmpty());

        System.out.println("\nRegistro eliminado");
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminarSinConfirmacion() throws Exception {
        Assertions.assertThrows(BusinessRuleException.class, () -> compraConfiteriaServicio.eliminar(1, false));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() throws Exception {
        Optional<CompraConfiteriaResponse> buscado = compraConfiteriaServicio.obtener(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\nRegistro obtenido:");
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {
        List<CompraConfiteriaResponse> items = compraConfiteriaServicio.listar();

        Assertions.assertEquals(8, items.size());

        System.out.println("\nListado de registros:");
        items.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {
        List<CompraConfiteriaResponse> items = compraConfiteriaServicio.listarPaginado();

        Assertions.assertEquals(8, items.size());

        System.out.println("\nListado paginado:");
        items.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorCompra() throws Exception {
        List<CompraConfiteriaResponse> items = compraConfiteriaServicio.listarPorCompra(1);

        Assertions.assertEquals(2, items.size());

        System.out.println("\nItems de confiteria por compra:");
        items.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorPresentacion() throws Exception {
        List<CompraConfiteriaResponse> items = compraConfiteriaServicio.listarPorPresentacion(1);

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
