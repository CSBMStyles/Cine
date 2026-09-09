package com.unicine.service.purchase;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.purchase.Compra;
import com.unicine.entity.purchase.CompraConfiteria;
import com.unicine.entity.purchase.CuponCliente;
import com.unicine.entity.purchase.Entrada;
import com.unicine.entity.user.Cliente;
import com.unicine.entity.showing.Funcion;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.purchase.CompraConfiteriaRepo;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.repository.purchase.CuponClienteRepo;
import com.unicine.repository.purchase.EntradaRepo;
import com.unicine.repository.showing.FuncionRepo;
import com.unicine.repository.user.ClienteRepo;
import com.unicine.transfer.dto.request.CompraCompletaRequest;
import com.unicine.transfer.dto.request.CompraRequest;
import com.unicine.transfer.dto.request.EntradaRequest;
import com.unicine.transfer.dto.response.CompraResponse;
import com.unicine.transfer.mapper.CompraConfiteriaMapper;
import com.unicine.transfer.mapper.CompraMapper;
import com.unicine.transfer.mapper.EntradaMapper;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;
import com.unicine.util.validation.catalog.domain.ShowingErrorCatalog;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;

/**
 * Implementacion del servicio de compras con logica de negocio completa.
 * Incluye validacion de cupones, calculo de totales, disponibilidad de sillas
 * y control de estados de compra.
 */
@Service
@Validated
public class CompraServicioImp implements CompraServicio {

    private final CompraRepo compraRepo;
    private final EntradaRepo entradaRepo;
    private final CompraConfiteriaRepo compraConfiteriaRepo;
    private final CuponClienteRepo cuponClienteRepo;
    private final ClienteRepo clienteRepo;
    private final FuncionRepo funcionRepo;
    private final EntradaServicio entradaServicio;
    private final CompraConfiteriaServicio compraConfiteriaServicio;
    private final CompraMapper compraMapper;
    private final EntradaMapper entradaMapper;
    private final CompraConfiteriaMapper compraConfiteriaMapper;

    public CompraServicioImp(CompraRepo compraRepo, EntradaRepo entradaRepo,
                             CompraConfiteriaRepo compraConfiteriaRepo,
                             CuponClienteRepo cuponClienteRepo, ClienteRepo clienteRepo,
                             FuncionRepo funcionRepo, EntradaServicio entradaServicio,
                             CompraConfiteriaServicio compraConfiteriaServicio,
                             CompraMapper compraMapper, EntradaMapper entradaMapper,
                             CompraConfiteriaMapper compraConfiteriaMapper) {
        this.compraRepo = compraRepo;
        this.entradaRepo = entradaRepo;
        this.compraConfiteriaRepo = compraConfiteriaRepo;
        this.cuponClienteRepo = cuponClienteRepo;
        this.clienteRepo = clienteRepo;
        this.funcionRepo = funcionRepo;
        this.entradaServicio = entradaServicio;
        this.compraConfiteriaServicio = compraConfiteriaServicio;
        this.compraMapper = compraMapper;
        this.entradaMapper = entradaMapper;
        this.compraConfiteriaMapper = compraConfiteriaMapper;
    }

    // SECTION: Metodos de soporte

    private void validarExiste(Optional<Compra> compra) {
        if (compra.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND);
        }
    }

    private void validarExiste(List<Compra> compras) {
        if (compras.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND);
        }
    }

    private void validarClienteExiste(Integer cedula) {
        Optional<Cliente> cliente = clienteRepo.findById(cedula);
        if (cliente.isEmpty()) {
            throw new ResourceNotFoundException(UserErrorCatalog.DOMAIN_USER_ENTITY_CLIENT_NOT_FOUND);
        }
    }

    private void validarFuncionExiste(Integer codigo) {
        Optional<Funcion> funcion = funcionRepo.findById(codigo);
        if (funcion.isEmpty()) {
            throw new ResourceNotFoundException(ShowingErrorCatalog.DOMAIN_SHOWING_ENTITY_FUNCTION_NOT_FOUND);
        }
    }

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

    private void validarDescuentoNoMayorTotal(Double descuento, Double total) {
        if (descuento > total) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_DISCOUNT_GREATER_THAN_TOTAL);
        }
    }

    private void validarCompraNoProcesada(Compra compra) {
        if (!compra.getEstado()) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_PURCHASE_ALREADY_PROCESSED);
        }
    }

    private void validarSillasDisponibles(List<Entrada> entradas, Integer codigoFuncion) {
        for (Entrada entrada : entradas) {
            boolean ocupada = entradaRepo.existsByFilaAndColumnaAndFuncionCodigo(
                    entrada.getFila(), entrada.getColumna(), codigoFuncion);
            if (ocupada) {
                throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_SELECTED_SEAT_ALREADY_OCCUPIED);
            }
        }
    }

    /**
     * Calcula el total de la compra separando entradas, confiteria y descuento.
     */
    private Double calcularValorTotal(List<Entrada> entradas,
                                      List<CompraConfiteria> confiterias,
                                      CuponCliente cuponCliente) {
        double totalEntradas = calcularTotalEntradas(entradas);
        double totalConfiteria = calcularTotalConfiteria(confiterias);
        return aplicarDescuento(totalEntradas + totalConfiteria, cuponCliente);
    }

    private double calcularTotalEntradas(List<Entrada> entradas) {
        return entradas.stream()
                .mapToDouble(Entrada::getPrecio)
                .sum();
    }

    private double calcularTotalConfiteria(List<CompraConfiteria> confiterias) {
        return confiterias.stream()
                .mapToDouble(confiteria -> confiteria.getPrecio() * confiteria.getUnidades())
                .sum();
    }

    /**
     * Aplica el descuento del cupon y evita que el descuento supere el subtotal.
     */
    private double aplicarDescuento(double subtotal, CuponCliente cuponCliente) {
        if (cuponCliente == null) {
            return subtotal;
        }

        double descuento = subtotal * cuponCliente.getCupon().getDescuento();
        validarDescuentoNoMayorTotal(descuento, subtotal);
        return subtotal - descuento;
    }

    private void validarDatosCompra(CompraRequest request) {
        validarClienteExiste(request.getClienteCedula());
        validarFuncionExiste(request.getFuncionCodigo());
    }

    private List<Entrada> prepararEntradas(List<EntradaRequest> requests, Integer codigoFuncion) {
        List<Entrada> entradas = entradaMapper.toEntityList(requests);
        validarSillasDisponibles(entradas, codigoFuncion);
        return entradas;
    }

    /**
     * Obtiene el cupon asignado y comprueba que pueda utilizarse en la compra.
     */
    private CuponCliente obtenerCuponCliente(Integer codigo) {
        if (codigo == null) {
            return null;
        }

        CuponCliente cuponCliente = cuponClienteRepo.findById(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND));
        validarCuponDisponible(cuponCliente);
        return cuponCliente;
    }

    private Compra construirCompra(CompraRequest request, Double valorTotal, CuponCliente cuponCliente) {
        Compra compra = compraMapper.toEntity(request);
        compra.setValorTotal(valorTotal);
        compra.setFechaCompra(LocalDateTime.now(ZoneId.of("America/Bogota")));
        compra.setEstado(true);
        // Siempre fijar (null incluido): el mapper puede dejar un CuponCliente
        // transitorio cuando cuponClienteCodigo es null y romperia el flush.
        compra.setCuponCliente(cuponCliente);
        return compra;
    }

    private void guardarEntradas(List<Entrada> entradas, Compra compra) {
        for (Entrada entrada : entradas) {
            entrada.setCompra(compra);
            entrada.setFuncion(compra.getFuncion());
            entradaRepo.save(entrada);
        }
    }

    private void guardarConfiterias(List<CompraConfiteria> confiterias, Compra compra) {
        for (CompraConfiteria confiteria : confiterias) {
            confiteria.setCompra(compra);
            compraConfiteriaRepo.save(confiteria);
        }
    }

    private void consumirCupon(CuponCliente cuponCliente) {
        if (cuponCliente == null) {
            return;
        }

        cuponCliente.setEstado(false);
        cuponClienteRepo.save(cuponCliente);
    }

    // !SECTION
    // SECTION: Implementacion de servicios Crud

    @Override
    @Transactional
    public CompraResponse registrar(CompraRequest request) {
        validarDatosCompra(request);
        CuponCliente cuponCliente = obtenerCuponCliente(request.getCuponClienteCodigo());
        Double valorTotal = calcularValorTotal(List.of(), List.of(), cuponCliente);
        Compra compra = construirCompra(request, valorTotal, cuponCliente);
        Compra registro = compraRepo.save(compra);
        consumirCupon(cuponCliente);
        return compraMapper.toResponse(registro);
    }

    @Override
    public CompraResponse actualizar(CompraRequest request) {
        Optional<Compra> buscado = compraRepo.findById(request.getCodigo());
        validarExiste(buscado);
        validarCompraNoProcesada(buscado.get());

        Compra compra = compraMapper.toEntity(request);
        Compra actualizado = compraRepo.save(compra);
        return compraMapper.toResponse(actualizado);
    }

    @Override
    public void eliminar(Integer codigo) {
        Optional<Compra> buscado = compraRepo.findById(codigo);
        validarExiste(buscado);
        compraRepo.delete(buscado.get());
    }

    @Override
    public Optional<CompraResponse> obtener(Integer codigo) {
        Optional<Compra> buscado = compraRepo.findById(codigo);
        validarExiste(buscado);
        return buscado.map(compraMapper::toResponse);
    }

    @Override
    public List<CompraResponse> listar() {
        return compraMapper.toResponseList(compraRepo.findAll());
    }

    @Override
    public List<CompraResponse> listarPaginado() {
        return compraMapper.toResponseList(compraRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    // !SECTION
    // SECTION: Implementacion de metodos de negocio

    /**
     * Registra una compra con sus entradas, productos de confiteria y cupon opcional.
     * Invariantes: ignora valorTotal y precio de entradas del cliente, calcula server-side,
     * valida sillas atomico, idempotente por codigo, transaccional.
     */
    @Override
    @Transactional
    public CompraResponse registrarCompraCompleta(CompraCompletaRequest request) throws Exception {
        CompraRequest compraRequest = request.getCompra();

        // Idempotencia por codigo cliente (reintento seguro)
        if (compraRequest.getCodigo() != null && compraRepo.existsById(compraRequest.getCodigo())) {
            return compraRepo.findById(compraRequest.getCodigo())
                    .map(compraMapper::toResponse)
                    .orElseThrow(() -> new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND));
        }

        validarDatosCompra(compraRequest);

        // Precio server-side: forzar precio de funcion para cada entrada
        Funcion funcion = funcionRepo.findById(compraRequest.getFuncionCodigo())
                .orElseThrow(() -> new ResourceNotFoundException(ShowingErrorCatalog.DOMAIN_SHOWING_ENTITY_FUNCTION_NOT_FOUND));
        List<Entrada> entradas = prepararEntradasConPrecioFuncion(request.getEntradas(), funcion);
        CuponCliente cuponCliente = obtenerCuponCliente(compraRequest.getCuponClienteCodigo());
        List<CompraConfiteria> confiterias = compraConfiteriaMapper.toEntityList(request.getConfiterias());

        Double valorTotal = calcularValorTotal(entradas, confiterias, cuponCliente);

        // Conflicto concurrente (silla tomada entre validacion e insercion,
        // o mismo codigo en carrera): el unique fila+columna+funcion lo frena
        // en flush y se traduce a 400 en vez de 500. Hace rollback total.
        try {
            Compra guardada = compraRepo.save(construirCompra(compraRequest, valorTotal, cuponCliente));
            guardarEntradas(entradas, guardada);
            entradaRepo.flush();
            guardarConfiterias(confiterias, guardada);
            consumirCupon(cuponCliente);

            return compraMapper.toResponse(guardada);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException(
                    PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_SELECTED_SEAT_ALREADY_OCCUPIED);
        }
    }

    private List<Entrada> prepararEntradasConPrecioFuncion(List<EntradaRequest> requests, Funcion funcion) {
        List<Entrada> entradas = entradaMapper.toEntityList(requests);
        // Sobrescribir precio cliente con precio real de funcion
        for (Entrada entrada : entradas) {
            entrada.setPrecio(funcion.getPrecio());
            entrada.setFuncion(funcion);
        }
        validarSillasDisponibles(entradas, funcion.getCodigo());
        return entradas;
    }

    @Override
    public List<CompraResponse> obtenerComprasCliente(Integer cedula) {
        List<Compra> compras = compraRepo.obtenerComprasCedula(cedula);
        validarExiste(compras);
        return compraMapper.toResponseList(compras);
    }

    @Override
    public Double obtenerTotalComprasCliente(Integer cedula) {
        Double total = compraRepo.obtenerTotalCompras(cedula);
        if (total == null) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND);
        }
        return total;
    }
    // !SECTION
}
