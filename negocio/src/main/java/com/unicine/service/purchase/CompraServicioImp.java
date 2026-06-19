package com.unicine.service.purchase;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.purchase.Compra;
import com.unicine.entity.purchase.CompraConfiteria;
import com.unicine.entity.purchase.CuponCliente;
import com.unicine.entity.purchase.Entrada;
import com.unicine.entity.user.Cliente;
import com.unicine.entity.showing.Funcion;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.repository.purchase.CuponClienteRepo;
import com.unicine.repository.purchase.EntradaRepo;
import com.unicine.repository.showing.FuncionRepo;
import com.unicine.service.purchase.CompraConfiteriaServicio;
import com.unicine.service.purchase.EntradaServicio;
import com.unicine.repository.user.ClienteRepo;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;
import com.unicine.util.validation.catalog.domain.ShowingErrorCatalog;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;

import jakarta.validation.Valid;

/**
 * Implementacion del servicio de compras con logica de negocio completa.
 * Incluye validacion de cupones, calculo de totales, disponibilidad de sillas
 * y control de estados de compra.
 */
@Service
@Validated
public class CompraServicioImp implements CompraServicio {

    // NOTE: Inyeccion por constructor recomendada sobre @Autowired
    private final CompraRepo compraRepo;
    private final EntradaRepo entradaRepo;
    private final CuponClienteRepo cuponClienteRepo;
    private final ClienteRepo clienteRepo;
    private final FuncionRepo funcionRepo;
    private final EntradaServicio entradaServicio;
    private final CompraConfiteriaServicio compraConfiteriaServicio;

    public CompraServicioImp(CompraRepo compraRepo, EntradaRepo entradaRepo,
                             CuponClienteRepo cuponClienteRepo, ClienteRepo clienteRepo,
                             FuncionRepo funcionRepo, EntradaServicio entradaServicio,
                             CompraConfiteriaServicio compraConfiteriaServicio) {
        this.compraRepo = compraRepo;
        this.entradaRepo = entradaRepo;
        this.cuponClienteRepo = cuponClienteRepo;
        this.clienteRepo = clienteRepo;
        this.funcionRepo = funcionRepo;
        this.entradaServicio = entradaServicio;
        this.compraConfiteriaServicio = compraConfiteriaServicio;
    }

    // SECTION: Metodos de soporte

    /**
     * Valida que la compra exista en la base de datos.
     * Lanza ResourceNotFoundException si no se encuentra.
     */
    private void validarExiste(Optional<Compra> compra) {
        if (compra.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND);
        }
    }

    /**
     * Valida que la lista de compras no este vacia.
     * Lanza ResourceNotFoundException si la lista esta vacia.
     */
    private void validarExiste(List<Compra> compras) {
        if (compras.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND);
        }
    }

    /**
     * Valida que el cliente exista en la base de datos.
     */
    private void validarClienteExiste(Integer cedula) {
        Optional<Cliente> cliente = clienteRepo.findById(cedula);
        if (cliente.isEmpty()) {
            throw new ResourceNotFoundException(UserErrorCatalog.DOMAIN_USER_ENTITY_CLIENT_NOT_FOUND);
        }
    }

    /**
     * Valida que la funcion exista en la base de datos.
     */
    private void validarFuncionExiste(Integer codigo) {
        Optional<Funcion> funcion = funcionRepo.findById(codigo);
        if (funcion.isEmpty()) {
            throw new ResourceNotFoundException(ShowingErrorCatalog.DOMAIN_SHOWING_ENTITY_FUNCTION_NOT_FOUND);
        }
    }

    /**
     * Valida que el cupon del cliente este disponible (no usado y no vencido).
     * Lanza BusinessRuleException si el cupon ya fue usado o si expiro.
     */
    private void validarCuponDisponible(CuponCliente cuponCliente) {
        if (cuponCliente == null) {
            return;
        }

        if (!cuponCliente.getEstado()) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_COUPON_ALREADY_USED);
        }

        if (cuponCliente.getCupon().getFechaVencimiento().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_COUPON_EXPIRED);
        }
    }

    /**
     * Valida que el descuento no supere el valor total de la compra.
     */
    private void validarDescuentoNoMayorTotal(Double descuento, Double total) {
        if (descuento > total) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_DISCOUNT_GREATER_THAN_TOTAL);
        }
    }

    /**
     * Valida que la compra no haya sido procesada (estado false).
     * Una compra procesada no puede modificarse.
     */
    private void validarCompraNoProcesada(Compra compra) {
        if (!compra.getEstado()) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_PURCHASE_ALREADY_PROCESSED);
        }
    }

    /**
     * Valida que las sillas solicitadas para una funcion esten disponibles.
     * Lanza BusinessRuleException si alguna silla ya esta ocupada.
     */
    private void validarSillasDisponibles(List<Entrada> entradas, Integer codigoFuncion) {
        for (Entrada entrada : entradas) {
            boolean ocupada = entradaRepo.existsByFilaAndColumnaAndFuncionCodigo(
                    entrada.getFila(), entrada.getColumna(), codigoFuncion);
            if (ocupada) {
                throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_ROOM_NOT_ENOUGH_AVAILABLE_SEATS);
            }
        }
    }

    /**
     * Calcula el valor total de la compra sumando entradas, confiteria
     * y aplicando el descuento del cupon si existe.
     */
    private Double calcularValorTotal(List<Entrada> entradas,
                                       List<CompraConfiteria> confiterias,
                                       CuponCliente cuponCliente) {
        double totalEntradas = entradas.stream()
                .mapToDouble(Entrada::getPrecio)
                .sum();

        double totalConfiteria = confiterias.stream()
                .mapToDouble(c -> c.getPrecio() * c.getUnidades())
                .sum();

        double subtotal = totalEntradas + totalConfiteria;

        if (cuponCliente != null) {
            double descuento = subtotal * cuponCliente.getCupon().getDescuento();
            validarDescuentoNoMayorTotal(descuento, subtotal);
            subtotal -= descuento;
        }

        return subtotal;
    }

    // SECTION: Implementacion de servicios CRUD

    @Override
    public Compra registrar(@Valid Compra compra) {
        return compraRepo.save(compra);
    }

    @Override
    public Compra actualizar(@Valid Compra compra) {
        Optional<Compra> buscado = compraRepo.findById(compra.getCodigo());
        validarExiste(buscado);
        validarCompraNoProcesada(buscado.get());
        return compraRepo.save(compra);
    }

    @Override
    public void eliminar(@Valid Compra compra) {
        compraRepo.delete(compra);
    }

    @Override
    public Optional<Compra> obtener(Integer codigo) {
        Optional<Compra> buscado = compraRepo.findById(codigo);
        validarExiste(buscado);
        return buscado;
    }

    @Override
    public List<Compra> listar() {
        return compraRepo.findAll();
    }

    @Override
    public List<Compra> listarPaginado() {
        return compraRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    // SECTION: Implementacion de metodos de negocio

    @Override
    public Compra registrarCompraCompleta(Compra compra,
                                           List<Entrada> entradas,
                                           List<CompraConfiteria> confiterias) throws Exception {
        validarClienteExiste(compra.getCliente().getCedula());
        validarFuncionExiste(compra.getFuncion().getCodigo());
        validarSillasDisponibles(entradas, compra.getFuncion().getCodigo());

        CuponCliente cuponCliente = compra.getCuponCliente();
        if (cuponCliente != null) {
            Optional<CuponCliente> cc = cuponClienteRepo.findById(cuponCliente.getCodigo());
            if (cc.isEmpty()) {
                throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND);
            }
            cuponCliente = cc.get();
            validarCuponDisponible(cuponCliente);
            compra.setCuponCliente(cuponCliente);
        }

        Double valorTotal = calcularValorTotal(entradas, confiterias, cuponCliente);
        compra.setValorTotal(valorTotal);
        compra.setFechaCompra(LocalDateTime.now(ZoneId.of("America/Bogota")));
        compra.setEstado(true);

        Compra guardada = compraRepo.save(compra);

        for (Entrada entrada : entradas) {
            entrada.setCompra(guardada);
            entrada.setFuncion(guardada.getFuncion());
            entradaServicio.registrar(entrada);
        }

        for (CompraConfiteria cc : confiterias) {
            cc.setCompra(guardada);
            compraConfiteriaServicio.registrar(cc);
        }

        if (cuponCliente != null) {
            cuponCliente.setEstado(false);
            cuponClienteRepo.save(cuponCliente);
        }

        return guardada;
    }

    @Override
    public List<Compra> obtenerComprasCliente(Integer cedula) {
        List<Compra> compras = compraRepo.obtenerComprasCedula(cedula);
        validarExiste(compras);
        return compras;
    }

    @Override
    public Double obtenerTotalComprasCliente(Integer cedula) {
        Double total = compraRepo.obtenerTotalCompras(cedula);
        if (total == null) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND);
        }
        return total;
    }
}
