package com.unicine.service.confiteria;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.confiteria.Confiteria;
import com.unicine.enums.confiteria.CategoriaConfiteria;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.confiteria.ConfiteriaRepo;
import com.unicine.transfer.dto.request.ConfiteriaRequest;
import com.unicine.transfer.dto.response.ConfiteriaResponse;
import com.unicine.transfer.mapper.ConfiteriaMapper;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

/**
 * Implementacion del servicio de confiteria con logica de negocio completa.
 * 
 * Gestiona el catalogo de productos clasificados por categoria
 * y permite consultas por nombre y tipo de producto.
 */
@Service
@Validated
public class ConfiteriaServicioImp implements ConfiteriaServicio {

    private final ConfiteriaRepo confiteriaRepo;

    private final ConfiteriaMapper confiteriaMapper;

    public ConfiteriaServicioImp(ConfiteriaRepo confiteriaRepo, ConfiteriaMapper confiteriaMapper) {
        this.confiteriaRepo = confiteriaRepo;
        this.confiteriaMapper = confiteriaMapper;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia de la confiteria que se esta buscando.
     * Lanza ResourceNotFoundException si no se encuentra.
     */
    private void validarExiste(Optional<Confiteria> confiteria) throws Exception {
        if (confiteria.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND);
        }
    }

    /**
     * Metodo para comprobar que la lista de confiterias no este vacia.
     * Lanza ResourceNotFoundException si la lista esta vacia.
     */
    private void validarExiste(List<Confiteria> confiterias) throws Exception {
        if (confiterias.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND);
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

    // !SECTION
    // SECTION: Implementacion de servicios Crud

    @Override
    public ConfiteriaResponse registrar(ConfiteriaRequest request) throws Exception {
        Confiteria confiteria = confiteriaMapper.toEntity(request);
        Confiteria registro = confiteriaRepo.save(confiteria);
        return confiteriaMapper.toResponse(registro);
    }

    @Override
    public ConfiteriaResponse actualizar(ConfiteriaRequest request) throws Exception {
        Optional<Confiteria> buscado = confiteriaRepo.findById(request.getCodigo());
        validarExiste(buscado);

        Confiteria confiteria = confiteriaMapper.toEntity(request);
        Confiteria actualizado = confiteriaRepo.save(confiteria);
        return confiteriaMapper.toResponse(actualizado);
    }

    @Override
    public void eliminar(Integer codigo, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);

        Optional<Confiteria> buscado = confiteriaRepo.findById(codigo);
        validarExiste(buscado);

        confiteriaRepo.delete(buscado.get());
    }

    @Override
    public Optional<ConfiteriaResponse> obtener(Integer codigo) throws Exception {
        Optional<Confiteria> buscado = confiteriaRepo.findById(codigo);
        validarExiste(buscado);
        return buscado.map(confiteriaMapper::toResponse);
    }

    @Override
    public List<ConfiteriaResponse> listar() {
        return confiteriaMapper.toResponseList(confiteriaRepo.findAll());
    }

    @Override
    public List<ConfiteriaResponse> listarPaginado() {
        return confiteriaMapper.toResponseList(confiteriaRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    // !SECTION
    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<ConfiteriaResponse> listarPorCategoria(CategoriaConfiteria categoria) throws Exception {
        List<Confiteria> confiterias = confiteriaRepo.findByCategoria(categoria);
        validarExiste(confiterias);
        return confiteriaMapper.toResponseList(confiterias);
    }

    @Override
    public List<ConfiteriaResponse> buscarPorNombre(String nombre) throws Exception {
        List<Confiteria> confiterias = confiteriaRepo.buscarPorNombre(nombre);
        validarExiste(confiterias);
        return confiteriaMapper.toResponseList(confiterias);
    }
    // !SECTION
}
