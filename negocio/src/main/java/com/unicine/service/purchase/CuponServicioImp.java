package com.unicine.service.purchase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.purchase.Cupon;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.purchase.CuponRepo;
import com.unicine.transfer.dto.request.CuponRequest;
import com.unicine.transfer.dto.response.CuponResponse;
import com.unicine.transfer.mapper.CuponMapper;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

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

    private final CuponMapper cuponMapper;

    public CuponServicioImp(CuponRepo cuponRepo, CuponMapper cuponMapper) {
        this.cuponRepo = cuponRepo;
        this.cuponMapper = cuponMapper;
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
     * Lanza BusinessRuleException con el catalogo de dominio si no se confirma.
     */
    private void comprobarConfirmacion(boolean confirmacion) throws Exception {
        if (!confirmacion) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_DELETE_DELETE_NOT_CONFIRMED);
        }
    }

    // !SECTION
    // SECTION: Implementacion de servicios Crud

    @Override
    public CuponResponse registrar(CuponRequest request) throws Exception {
        Cupon cupon = cuponMapper.toEntity(request);
        Cupon registro = cuponRepo.save(cupon);
        return cuponMapper.toResponse(registro);
    }

    @Override
    public CuponResponse actualizar(CuponRequest request) throws Exception {
        Optional<Cupon> buscado = cuponRepo.findById(request.getCodigo());
        validarExiste(buscado);

        Cupon cupon = cuponMapper.toEntity(request);
        Cupon actualizado = cuponRepo.save(cupon);
        return cuponMapper.toResponse(actualizado);
    }

    @Override
    public void eliminar(Integer codigo, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);

        Optional<Cupon> buscado = cuponRepo.findById(codigo);
        validarExiste(buscado);

        cuponRepo.delete(buscado.get());
    }

    @Override
    public Optional<CuponResponse> obtener(Integer codigo) throws Exception {
        Optional<Cupon> buscado = cuponRepo.findById(codigo);
        validarExiste(buscado);
        return buscado.map(cuponMapper::toResponse);
    }

    @Override
    public List<CuponResponse> listar() {
        return cuponMapper.toResponseList(cuponRepo.findAll());
    }

    @Override
    public List<CuponResponse> listarPaginado() {
        return cuponMapper.toResponseList(cuponRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    // !SECTION
    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<CuponResponse> listarActivos() throws Exception {
        List<Cupon> cupones = cuponRepo.findByFechaVencimientoAfter(LocalDateTime.now());
        validarExiste(cupones);
        return cuponMapper.toResponseList(cupones);
    }

    @Override
    public List<CuponResponse> listarVencidos() throws Exception {
        List<Cupon> cupones = cuponRepo.findByFechaVencimientoBefore(LocalDateTime.now());
        validarExiste(cupones);
        return cuponMapper.toResponseList(cupones);
    }

    @Override
    public List<CuponResponse> buscarPorCriterio(String criterio) throws Exception {
        List<Cupon> cupones = cuponRepo.findByCriterioContainingIgnoreCase(criterio);
        validarExiste(cupones);
        return cuponMapper.toResponseList(cupones);
    }

    @Override
    public List<CuponResponse> listarPorRangoDescuento(Double min, Double max) throws Exception {
        List<Cupon> cupones = cuponRepo.findByDescuentoBetween(min, max);
        validarExiste(cupones);
        return cuponMapper.toResponseList(cupones);
    }

    @Override
    public List<CuponResponse> listarConAsignaciones() throws Exception {
        List<Cupon> cupones = cuponRepo.findConAsignaciones();
        validarExiste(cupones);
        return cuponMapper.toResponseList(cupones);
    }
    // !SECTION
}
