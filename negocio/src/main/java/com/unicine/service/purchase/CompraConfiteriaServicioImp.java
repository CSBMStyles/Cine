package com.unicine.service.purchase;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.confiteria.ConfiteriaPresentacion;
import com.unicine.entity.purchase.Compra;
import com.unicine.entity.purchase.CompraConfiteria;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.confiteria.ConfiteriaPresentacionRepo;
import com.unicine.repository.purchase.CompraConfiteriaRepo;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.transfer.dto.request.CompraConfiteriaRequest;
import com.unicine.transfer.dto.response.CompraConfiteriaResponse;
import com.unicine.transfer.mapper.CompraConfiteriaMapper;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

/**
 * Implementacion del servicio de items de confiteria dentro de una compra.
 * 
 * Gestiona el registro, actualizacion, consulta y eliminacion de items,
 * validando la existencia de la compra y la presentacion de confiteria asociadas.
 */
@Service
@Validated
public class CompraConfiteriaServicioImp implements CompraConfiteriaServicio {

    private final CompraConfiteriaRepo compraConfiteriaRepo;
    private final CompraRepo compraRepo;
    private final ConfiteriaPresentacionRepo presentacionRepo;
    private final CompraConfiteriaMapper compraConfiteriaMapper;

    public CompraConfiteriaServicioImp(CompraConfiteriaRepo compraConfiteriaRepo,
                                       CompraRepo compraRepo,
                                       ConfiteriaPresentacionRepo presentacionRepo,
                                       CompraConfiteriaMapper compraConfiteriaMapper) {
        this.compraConfiteriaRepo = compraConfiteriaRepo;
        this.compraRepo = compraRepo;
        this.presentacionRepo = presentacionRepo;
        this.compraConfiteriaMapper = compraConfiteriaMapper;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia del item de confiteria que se esta buscando.
     * Lanza ResourceNotFoundException si no se encuentra.
     */
    private void validarExiste(Optional<CompraConfiteria> compraConfiteria) {
        if (compraConfiteria.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND);
        }
    }

    /**
     * Metodo para comprobar que la lista de items no este vacia.
     * Lanza ResourceNotFoundException si la lista esta vacia.
     */
    private void validarExiste(List<CompraConfiteria> compraConfiterias) {
        if (compraConfiterias.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND);
        }
    }

    /**
     * Valida que la compra exista en la base de datos.
     */
    private void validarCompraExiste(Integer codigo) {
        Optional<Compra> compra = compraRepo.findById(codigo);
        if (compra.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND);
        }
    }

    /**
     * Valida que la presentacion de confiteria exista en la base de datos.
     */
    private void validarPresentacionExiste(Integer codigo) {
        Optional<ConfiteriaPresentacion> presentacion = presentacionRepo.findById(codigo);
        if (presentacion.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_PRESENTATION_NOT_FOUND);
        }
    }

    /**
     * Metodo para validar la confirmacion de la eliminacion.
     */
    private void comprobarConfirmacion(boolean confirmacion) throws Exception {
        if (!confirmacion) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_DELETE_DELETE_NOT_CONFIRMED);
        }
    }

    /**
     * Valida que la compra y la presentacion asociadas al item existan.
     */
    private void validarRelaciones(CompraConfiteria compraConfiteria) {
        validarCompraExiste(compraConfiteria.getCompra().getCodigo());
        validarPresentacionExiste(compraConfiteria.getPresentacion().getCodigo());
    }

    // !SECTION
    // SECTION: Implementacion de servicios Crud

    @Override
    public CompraConfiteriaResponse registrar(CompraConfiteriaRequest request) {
        CompraConfiteria compraConfiteria = compraConfiteriaMapper.toEntity(request);
        validarRelaciones(compraConfiteria);
        CompraConfiteria registro = compraConfiteriaRepo.save(compraConfiteria);
        return compraConfiteriaMapper.toResponse(registro);
    }

    @Override
    public CompraConfiteriaResponse actualizar(CompraConfiteriaRequest request) {
        Optional<CompraConfiteria> buscado = compraConfiteriaRepo.findById(request.getCodigo());
        validarExiste(buscado);

        CompraConfiteria compraConfiteria = compraConfiteriaMapper.toEntity(request);
        validarRelaciones(compraConfiteria);
        CompraConfiteria actualizado = compraConfiteriaRepo.save(compraConfiteria);
        return compraConfiteriaMapper.toResponse(actualizado);
    }

    @Override
    public void eliminar(Integer codigo, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);

        Optional<CompraConfiteria> buscado = compraConfiteriaRepo.findById(codigo);
        validarExiste(buscado);

        compraConfiteriaRepo.delete(buscado.get());
    }

    @Override
    public Optional<CompraConfiteriaResponse> obtener(Integer codigo) {
        Optional<CompraConfiteria> buscado = compraConfiteriaRepo.findById(codigo);
        validarExiste(buscado);
        return buscado.map(compraConfiteriaMapper::toResponse);
    }

    @Override
    public List<CompraConfiteriaResponse> listar() {
        return compraConfiteriaMapper.toResponseList(compraConfiteriaRepo.findAll());
    }

    @Override
    public List<CompraConfiteriaResponse> listarPaginado() {
        return compraConfiteriaMapper.toResponseList(compraConfiteriaRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    // !SECTION
    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<CompraConfiteriaResponse> listarPorCompra(Integer codigoCompra) {
        validarCompraExiste(codigoCompra);
        List<CompraConfiteria> items = compraConfiteriaRepo.findByCompraCodigo(codigoCompra);
        validarExiste(items);
        return compraConfiteriaMapper.toResponseList(items);
    }

    @Override
    public List<CompraConfiteriaResponse> listarPorPresentacion(Integer codigoPresentacion) {
        validarPresentacionExiste(codigoPresentacion);
        List<CompraConfiteria> items = compraConfiteriaRepo.findByPresentacionCodigo(codigoPresentacion);
        validarExiste(items);
        return compraConfiteriaMapper.toResponseList(items);
    }

    @Override
    public Double calcularTotalPorCompra(Integer codigoCompra) {
        validarCompraExiste(codigoCompra);
        return compraConfiteriaRepo.calcularTotalPorCompra(codigoCompra);
    }
    // !SECTION
}
