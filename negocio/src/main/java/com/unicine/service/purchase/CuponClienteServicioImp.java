package com.unicine.service.purchase;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.purchase.CuponCliente;
import com.unicine.entity.user.Cliente;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.purchase.CuponClienteRepo;
import com.unicine.repository.user.ClienteRepo;
import com.unicine.transfer.dto.request.CuponClienteRequest;
import com.unicine.transfer.dto.response.CuponClienteResponse;
import com.unicine.transfer.mapper.CuponClienteMapper;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;

/**
 * Implementacion del servicio de cupones asignados a clientes.
 * 
 * Gestiona la disponibilidad de cupones por cliente y permite consultas
 * de asignaciones activas, inactivas y cupones redimidos.
 */
@Service
@Validated
public class CuponClienteServicioImp implements CuponClienteServicio {

    private final CuponClienteRepo cuponClienteRepo;
    private final ClienteRepo clienteRepo;
    private final CuponClienteMapper cuponClienteMapper;

    public CuponClienteServicioImp(CuponClienteRepo cuponClienteRepo, ClienteRepo clienteRepo, CuponClienteMapper cuponClienteMapper) {
        this.cuponClienteRepo = cuponClienteRepo;
        this.clienteRepo = clienteRepo;
        this.cuponClienteMapper = cuponClienteMapper;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia del cupon asignado que se esta buscando.
     * Lanza ResourceNotFoundException si no se encuentra.
     */
    private void validarExiste(Optional<CuponCliente> cuponCliente) throws Exception {
        if (cuponCliente.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND);
        }
    }

    /**
     * Metodo para comprobar que la lista de cupones asignados no este vacia.
     * Lanza ResourceNotFoundException si la lista esta vacia.
     */
    private void validarExiste(List<CuponCliente> cuponesClientes) throws Exception {
        if (cuponesClientes.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND);
        }
    }

    /**
     * Valida que el cliente exista en la base de datos.
     */
    private void validarClienteExiste(Integer cedula) throws Exception {
        Optional<Cliente> cliente = clienteRepo.findById(cedula);
        if (cliente.isEmpty()) {
            throw new ResourceNotFoundException(UserErrorCatalog.DOMAIN_USER_ENTITY_CLIENT_NOT_FOUND);
        }
    }

    /**
     * Metodo para validar la confirmacion de la eliminacion.
     * Lanza BusinessRuleException con el catalogo de dominio si no se confirma.
     */
    private void comprobarConfirmacion(boolean confirmacion) throws Exception {
        if (!confirmacion) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_DELETE_DELETE_NOT_CONFIRMED);
        }
    }

    // SECTION: Implementacion de servicios CRUD

    @Override
    public CuponClienteResponse registrar(CuponClienteRequest request) throws Exception {
        CuponCliente cuponCliente = cuponClienteMapper.toEntity(request);
        CuponCliente registro = cuponClienteRepo.save(cuponCliente);
        return cuponClienteMapper.toResponse(registro);
    }

    @Override
    public CuponClienteResponse actualizar(CuponClienteRequest request) throws Exception {
        Optional<CuponCliente> buscado = cuponClienteRepo.findById(request.getCodigo());
        validarExiste(buscado);

        CuponCliente cuponCliente = cuponClienteMapper.toEntity(request);
        CuponCliente actualizado = cuponClienteRepo.save(cuponCliente);
        return cuponClienteMapper.toResponse(actualizado);
    }

    @Override
    public void eliminar(Integer codigo, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);

        Optional<CuponCliente> buscado = cuponClienteRepo.findById(codigo);
        validarExiste(buscado);

        cuponClienteRepo.delete(buscado.get());
    }

    @Override
    public Optional<CuponClienteResponse> obtener(Integer codigo) throws Exception {
        Optional<CuponCliente> buscado = cuponClienteRepo.findById(codigo);
        validarExiste(buscado);
        return buscado.map(cuponClienteMapper::toResponse);
    }

    @Override
    public List<CuponClienteResponse> listar() {
        return cuponClienteMapper.toResponseList(cuponClienteRepo.findAll());
    }

    @Override
    public List<CuponClienteResponse> listarPaginado() {
        return cuponClienteMapper.toResponseList(cuponClienteRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<CuponClienteResponse> listarPorCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        List<CuponCliente> cuponesClientes = cuponClienteRepo.findByClienteCedula(cedula);
        validarExiste(cuponesClientes);
        return cuponClienteMapper.toResponseList(cuponesClientes);
    }

    @Override
    public List<CuponClienteResponse> listarActivosPorCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        List<CuponCliente> cuponesClientes = cuponClienteRepo.findByClienteCedulaAndEstado(cedula, true);
        validarExiste(cuponesClientes);
        return cuponClienteMapper.toResponseList(cuponesClientes);
    }

    @Override
    public List<CuponClienteResponse> listarInactivosPorCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        List<CuponCliente> cuponesClientes = cuponClienteRepo.findByClienteCedulaAndEstado(cedula, false);
        validarExiste(cuponesClientes);
        return cuponClienteMapper.toResponseList(cuponesClientes);
    }

    @Override
    public Optional<CuponClienteResponse> obtenerPorCuponYCliente(Integer codigoCupon, Integer cedula) throws Exception {
        Optional<CuponCliente> buscado = cuponClienteRepo.findByCuponCodigoAndClienteCedula(codigoCupon, cedula);
        validarExiste(buscado);
        return buscado.map(cuponClienteMapper::toResponse);
    }

    @Override
    public Long contarRedimidosPorCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        Long cantidad = cuponClienteRepo.contarRedimidosPorCliente(cedula);
        if (cantidad == null || cantidad == 0) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND);
        }
        return cantidad;
    }
}
