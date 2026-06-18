package com.unicine.service.purchase;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.purchase.CuponCliente;
import com.unicine.entity.user.Cliente;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.purchase.CuponClienteRepo;
import com.unicine.repository.user.ClienteRepo;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;

import jakarta.validation.Valid;

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

    public CuponClienteServicioImp(CuponClienteRepo cuponClienteRepo, ClienteRepo clienteRepo) {
        this.cuponClienteRepo = cuponClienteRepo;
        this.clienteRepo = clienteRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia del cupon asignado que se esta buscando.
     * Lanza ResourceNotFoundException si no se encuentra.
     */
    private void validarExiste(Optional<CuponCliente> cuponCliente) throws Exception {
        if (cuponCliente.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.ENT016);
        }
    }

    /**
     * Metodo para comprobar que la lista de cupones asignados no este vacia.
     * Lanza ResourceNotFoundException si la lista esta vacia.
     */
    private void validarExiste(List<CuponCliente> cuponesClientes) throws Exception {
        if (cuponesClientes.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.ENT016);
        }
    }

    /**
     * Valida que el cliente exista en la base de datos.
     */
    private void validarClienteExiste(Integer cedula) throws Exception {
        Optional<Cliente> cliente = clienteRepo.findById(cedula);
        if (cliente.isEmpty()) {
            throw new ResourceNotFoundException(UserErrorCatalog.ENT003);
        }
    }

    /**
     * Metodo para validar la confirmacion de la eliminacion.
     */
    private void comprobarConfirmacion(boolean confirmacion) throws Exception {
        if (!confirmacion) {
            throw new RuntimeException("La eliminacion no fue confirmada");
        }
    }

    // SECTION: Implementacion de servicios CRUD

    @Override
    public CuponCliente registrar(@Valid CuponCliente cuponCliente) throws Exception {
        return cuponClienteRepo.save(cuponCliente);
    }

    @Override
    public CuponCliente actualizar(@Valid CuponCliente cuponCliente) throws Exception {
        Optional<CuponCliente> buscado = cuponClienteRepo.findById(cuponCliente.getCodigo());
        validarExiste(buscado);
        return cuponClienteRepo.save(cuponCliente);
    }

    @Override
    public void eliminar(@Valid CuponCliente cuponCliente, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);
        cuponClienteRepo.delete(cuponCliente);
    }

    @Override
    public Optional<CuponCliente> obtener(Integer codigo) throws Exception {
        Optional<CuponCliente> buscado = cuponClienteRepo.findById(codigo);
        validarExiste(buscado);
        return buscado;
    }

    @Override
    public List<CuponCliente> listar() {
        return cuponClienteRepo.findAll();
    }

    @Override
    public List<CuponCliente> listarPaginado() {
        return cuponClienteRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<CuponCliente> listarPorCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        List<CuponCliente> cuponesClientes = cuponClienteRepo.findByClienteCedula(cedula);
        validarExiste(cuponesClientes);
        return cuponesClientes;
    }

    @Override
    public List<CuponCliente> listarActivosPorCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        List<CuponCliente> cuponesClientes = cuponClienteRepo.findByClienteCedulaAndEstado(cedula, true);
        validarExiste(cuponesClientes);
        return cuponesClientes;
    }

    @Override
    public List<CuponCliente> listarInactivosPorCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        List<CuponCliente> cuponesClientes = cuponClienteRepo.findByClienteCedulaAndEstado(cedula, false);
        validarExiste(cuponesClientes);
        return cuponesClientes;
    }

    @Override
    public Optional<CuponCliente> obtenerPorCuponYCliente(Integer codigoCupon, Integer cedula) throws Exception {
        Optional<CuponCliente> buscado = cuponClienteRepo.findByCuponCodigoAndClienteCedula(codigoCupon, cedula);
        validarExiste(buscado);
        return buscado;
    }

    @Override
    public Long contarRedimidosPorCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        Long cantidad = cuponClienteRepo.contarRedimidosPorCliente(cedula);
        if (cantidad == null || cantidad == 0) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.ENT016);
        }
        return cantidad;
    }
}
