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
import com.unicine.repository.purchase.CompraConfiteriaRepo;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.repository.purchase.CuponClienteRepo;
import com.unicine.repository.purchase.EntradaRepo;
import com.unicine.repository.showing.FuncionRepo;
import com.unicine.repository.user.ClienteRepo;
import com.unicine.transfer.dto.request.CompraCompletaRequest;
import com.unicine.transfer.dto.request.CompraConfiteriaRequest;
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
                throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_ROOM_NOT_ENOUGH_AVAILABLE_SEATS);
            }
        }
    }

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
    public CompraResponse registrar(CompraRequest request) {
        Compra compra = compraMapper.toEntity(request);
        Compra registro = compraRepo.save(compra);
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

    // SECTION: Implementacion de metodos de negocio

    @Override
    public CompraResponse registrarCompraCompleta(CompraCompletaRequest request) throws Exception {
        CompraRequest compraRequest = request.getCompra();
        List<EntradaRequest> entradaRequests = request.getEntradas();
        List<CompraConfiteriaRequest> confiteriaRequests = request.getConfiterias();

        validarClienteExiste(compraRequest.getClienteCedula());
        validarFuncionExiste(compraRequest.getFuncionCodigo());

        List<Entrada> entradas = entradaMapper.toEntityList(entradaRequests);
        validarSillasDisponibles(entradas, compraRequest.getFuncionCodigo());

        CuponCliente cuponCliente = null;
        if (compraRequest.getCuponClienteCodigo() != null) {
            Optional<CuponCliente> cc = cuponClienteRepo.findById(compraRequest.getCuponClienteCodigo());
            if (cc.isEmpty()) {
                throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND);
            }
            cuponCliente = cc.get();
            validarCuponDisponible(cuponCliente);
        }

        List<CompraConfiteria> confiterias = compraConfiteriaMapper.toEntityList(confiteriaRequests);

        Double valorTotal = calcularValorTotal(entradas, confiterias, cuponCliente);

        Compra compra = compraMapper.toEntity(compraRequest);
        compra.setValorTotal(valorTotal);
        compra.setFechaCompra(LocalDateTime.now(ZoneId.of("America/Bogota")));
        compra.setEstado(true);
        if (cuponCliente != null) {
            compra.setCuponCliente(cuponCliente);
        }

        Compra guardada = compraRepo.save(compra);

        for (Entrada entrada : entradas) {
            entrada.setCompra(guardada);
            entrada.setFuncion(guardada.getFuncion());
            entradaRepo.save(entrada);
        }

        for (CompraConfiteria cc : confiterias) {
            cc.setCompra(guardada);
            compraConfiteriaRepo.save(cc);
        }

        if (cuponCliente != null) {
            cuponCliente.setEstado(false);
            cuponClienteRepo.save(cuponCliente);
        }

        return compraMapper.toResponse(guardada);
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
}
