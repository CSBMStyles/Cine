package com.unicine.service.purchase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.purchase.Cupon;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.purchase.CuponRepo;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

import jakarta.validation.Valid;

/**
 * Implementacion del servicio de cupones con logica de negocio completa.
 * 
 * Gestiona los cupones de descuento globales y permite consultas por vigencia,
 * criterio, rango de descuento y asignaciones a clientes.
 */
@Service
@Validated
public class CuponServicioImp implements CuponServicio {

    private final CuponRepo cuponRepo;

    public CuponServicioImp(CuponRepo cuponRepo) {
        this.cuponRepo = cuponRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia del cupon que se esta buscando.
     * Lanza ResourceNotFoundException si no se encuentra.
     */
    private void validarExiste(Optional<Cupon> cupon) throws Exception {
        if (cupon.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND);
        }
    }

    /**
     * Metodo para comprobar que la lista de cupones no este vacia.
     * Lanza ResourceNotFoundException si la lista esta vacia.
     */
    private void validarExiste(List<Cupon> cupones) throws Exception {
        if (cupones.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND);
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
    public Cupon registrar(@Valid Cupon cupon) throws Exception {
        return cuponRepo.save(cupon);
    }

    @Override
    public Cupon actualizar(@Valid Cupon cupon) throws Exception {
        Optional<Cupon> buscado = cuponRepo.findById(cupon.getCodigo());
        validarExiste(buscado);
        return cuponRepo.save(cupon);
    }

    @Override
    public void eliminar(@Valid Cupon cupon, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);
        cuponRepo.delete(cupon);
    }

    @Override
    public Optional<Cupon> obtener(Integer codigo) throws Exception {
        Optional<Cupon> buscado = cuponRepo.findById(codigo);
        validarExiste(buscado);
        return buscado;
    }

    @Override
    public List<Cupon> listar() {
        return cuponRepo.findAll();
    }

    @Override
    public List<Cupon> listarPaginado() {
        return cuponRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<Cupon> listarActivos() throws Exception {
        List<Cupon> cupones = cuponRepo.findByFechaVencimientoAfter(LocalDateTime.now());
        validarExiste(cupones);
        return cupones;
    }

    @Override
    public List<Cupon> listarVencidos() throws Exception {
        List<Cupon> cupones = cuponRepo.findByFechaVencimientoBefore(LocalDateTime.now());
        validarExiste(cupones);
        return cupones;
    }

    @Override
    public List<Cupon> buscarPorCriterio(String criterio) throws Exception {
        List<Cupon> cupones = cuponRepo.findByCriterioContainingIgnoreCase(criterio);
        validarExiste(cupones);
        return cupones;
    }

    @Override
    public List<Cupon> listarPorRangoDescuento(Double min, Double max) throws Exception {
        List<Cupon> cupones = cuponRepo.findByDescuentoBetween(min, max);
        validarExiste(cupones);
        return cupones;
    }

    @Override
    public List<Cupon> listarConAsignaciones() throws Exception {
        List<Cupon> cupones = cuponRepo.findConAsignaciones();
        validarExiste(cupones);
        return cupones;
    }
}
