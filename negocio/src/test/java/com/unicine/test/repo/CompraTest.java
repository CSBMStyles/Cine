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

import com.unicine.dto.DetalleCompraDTO;
import com.unicine.entidades.Cliente;
import com.unicine.entidades.Compra;
import com.unicine.entidades.Cupon;
import com.unicine.entidades.CuponCliente;
import com.unicine.entidades.Funcion;
import com.unicine.entidades.MedioPago;
import com.unicine.repo.ClienteRepo;
import com.unicine.repo.CompraRepo;
import com.unicine.repo.CuponClienteRepo;
import com.unicine.repo.CuponRepo;
import com.unicine.repo.FuncionRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CompraTest {

    /*
     * NOTE: En las pruebas de unitarias o de integracion se menciona que se debe
     * comprobar el resultado con el Assertions, pero no esta de mas imprimir el
     * resultado para verificar visualmente que se esta obteniendo lo esperado
     */

    @Autowired
    private CompraRepo compraRepo;

    @Autowired
    private CuponRepo cuponRepo;

    @Autowired
    private ClienteRepo clienteRepo;

    @Autowired
    private FuncionRepo funcionRepo;

    @Autowired
    private CuponClienteRepo cuponClienteRepo;

    // SECTION: Consultas basicas para la base de datos

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        // SECTION: Se obtienen los datos necesarios para la creación de la compra
        MedioPago pago = MedioPago.valueOf("VISA");

        Cupon cupon = cuponRepo.findById(1).orElse(null);

        Cliente cliente = clienteRepo.findById(1009000011).orElse(null);

        Funcion funcion = funcionRepo.findById(1).orElse(null);

        CuponCliente cuponCliente = new CuponCliente(true, cupon, cliente);
        cuponCliente.setCodigo(6);

        CuponCliente cuponClienteSave = cuponClienteRepo.save(cuponCliente);

        // SECTION: Se construye la compra
        Compra compra = new Compra(pago, cuponClienteSave, cliente, funcion);

        compra.setFechaPelicula(funcion.getHorario().getFechaInicio());
        compra.setValorTotal(20000.0);
        compra.setCodigo(6);

        Compra guardado = compraRepo.save(compra);

        Assertions.assertNotNull(guardado);

        System.out.println("\n" + "Registro guardado:");

        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Compra guardado = compraRepo.findById(1).orElse(null);

        System.out.println(guardado);

        MedioPago pago = MedioPago.valueOf("MASTERCARD");
        guardado.setMedioPago(pago);

        Compra actualizado = compraRepo.save(guardado);

        Assertions.assertEquals("MASTERCARD", actualizado.getMedioPago().name());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Compra buscado = compraRepo.findById(1).orElse(null);

        System.out.println(buscado);

        compraRepo.delete(buscado);

        Compra verificacion = compraRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<Compra> buscado = compraRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");

        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<Compra> compras = compraRepo.findAll();

        Assertions.assertEquals(5, compras.size());

        System.out.println("\n" + "Listado de registros:");

        for (Compra c : compras) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Compra> compras = compraRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, compras.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (Compra c : compras) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<Compra> compras = compraRepo.findAll(Sort.by("codigo"));

        Assertions.assertEquals(5, compras.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (Compra c : compras) {
            System.out.println(c);
        }
    }

    // SECTION: Consultas personalizadas para la base de datos

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerComprasCedula() {

        List<Compra> compras = compraRepo.obtenerComprasCedula(1008000022);

        System.out.println("Resultado: \n");

        for (Compra c : compras) {
            Assertions.assertEquals(1008000022, c.getCliente().getCedula());

            System.out.println(c.getCodigo());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerComprasCorreo() {

        List<Compra> compras = compraRepo.obtenerComprasCorreo("juan@outlook.com");

        System.out.println("Resultado: \n");

        for (Compra c : compras) {
            Assertions.assertEquals("juan@outlook.com", c.getCliente().getCorreo());

            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerComprasClientes() {

        List<Object[]> comprasClientes = compraRepo.obtenerComprasClientes();

        System.out.println("Resultado: \n");

        for (Object[] compraCliente : comprasClientes) {
            System.out.println("Compra: " + compraCliente[0] + "\n" + "Cliente: " + compraCliente[1]);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerInformacionCompra() {

        List<DetalleCompraDTO> detallesCompra = compraRepo.obtenerInformacionCompra(1008000022);

        // Verifica que los detalles de la c sean correctos
        Assertions.assertFalse(detallesCompra.isEmpty(), "No se encontraron detalles de la c");

        System.out.println("Resultado: \n");

        for (DetalleCompraDTO detalle : detallesCompra) {
            System.out.println(detalle);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerPreciosEntradaCompra() {

        Integer codigoCompra = 1; // Reemplaza con un código de compra válido para tu dataset

        List<Double> preciosEntrada = compraRepo.obtenerPreciosEntradaCompra(codigoCompra);

        Assertions.assertEquals(1, preciosEntrada.size());

        System.out.println("Resultado: \n");

        System.out.println(preciosEntrada);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerPreciosConfiteriaCompra() {
        
        Integer codigoCompra = 1; // Reemplaza con un código de c válido para tu dataset

        List<Double> preciosConfiteria = compraRepo.obtenerPreciosConfiteriaCompra(codigoCompra);

        Assertions.assertEquals(2, preciosConfiteria.size());

        System.out.println("Resultado: \n");

        System.out.println(preciosConfiteria);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerTotalCompras() {

        Double totalCompras = compraRepo.obtenerTotalCompras(1008000022);

        Assertions.assertEquals(89000.0, totalCompras);

        System.out.println("Total del historico de compras que tiene el cliente: \n");

        System.out.println(totalCompras);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerCompraCostosa() {

        List<Object[]> comprasCostosas = compraRepo.obtenerCompraCostosa();

        Assertions.assertNotNull(comprasCostosas);

        System.out.println("Resultado: \n");

        for (Object[] mayorCompra : comprasCostosas) {
            System.out.println("Correo: " + mayorCompra[0] + "\n" + "Compra: " + mayorCompra[1]);
        }
    }
}
