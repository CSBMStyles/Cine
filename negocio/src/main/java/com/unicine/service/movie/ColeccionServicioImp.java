package com.unicine.service.movie;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.movie.Coleccion;
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.movie.composed.ColeccionCompuesta;
import com.unicine.entity.user.Cliente;
import com.unicine.enums.movie.EstadoPropio;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.movie.ColeccionRepo;
import com.unicine.repository.movie.PeliculaRepo;
import com.unicine.repository.user.ClienteRepo;
import com.unicine.transfer.dto.request.ColeccionRequest;
import com.unicine.transfer.dto.response.ColeccionResponse;
import com.unicine.transfer.mapper.ColeccionMapper;
import com.unicine.util.validation.catalog.domain.MovieErrorCatalog;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;

/**
 * Implementacion del servicio de colecciones con logica de negocio completa.
 *
 * Gestiona la relacion cliente-pelicula incluyendo puntuaciones,
 * estados de visionado y consultas por cliente o pelicula.
 */
@Service
@Validated
public class ColeccionServicioImp implements ColeccionServicio {

    private final ColeccionRepo coleccionRepo;
    private final ClienteRepo clienteRepo;
    private final PeliculaRepo peliculaRepo;
    private final ColeccionMapper coleccionMapper;

    public ColeccionServicioImp(ColeccionRepo coleccionRepo, ClienteRepo clienteRepo,
                                PeliculaRepo peliculaRepo, ColeccionMapper coleccionMapper) {
        this.coleccionRepo = coleccionRepo;
        this.clienteRepo = clienteRepo;
        this.peliculaRepo = peliculaRepo;
        this.coleccionMapper = coleccionMapper;
    }

    // SECTION: Metodos de soporte

    private void validarExiste(Optional<Coleccion> coleccion) throws Exception {
        if (coleccion.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_COLLECTION_NOT_FOUND);
        }
    }

    private void validarExiste(List<Coleccion> colecciones) throws Exception {
        if (colecciones.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_COLLECTION_NOT_FOUND);
        }
    }

    private void validarClienteExiste(Integer cedula) throws Exception {
        Optional<Cliente> cliente = clienteRepo.findById(cedula);
        if (cliente.isEmpty()) {
            throw new ResourceNotFoundException(UserErrorCatalog.DOMAIN_USER_ENTITY_CLIENT_NOT_FOUND);
        }
    }

    private void validarPeliculaExiste(Integer codigo) throws Exception {
        Optional<Pelicula> pelicula = peliculaRepo.findById(codigo);
        if (pelicula.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_MOVIE_NOT_FOUND);
        }
    }

    private void comprobarConfirmacion(boolean confirmacion) throws Exception {
        if (!confirmacion) {
            throw new RuntimeException("La eliminacion no fue confirmada");
        }
    }

    private ColeccionCompuesta construirId(Integer cedula, Integer codigoPelicula) {
        return new ColeccionCompuesta(cedula, codigoPelicula);
    }

    // !SECTION
    // SECTION: Implementacion de servicios Crud

    @Override
    public ColeccionResponse registrar(ColeccionRequest request) throws Exception {
        Coleccion coleccion = coleccionMapper.toEntity(request);

        validarClienteExiste(coleccion.getCliente().getCedula());
        validarPeliculaExiste(coleccion.getPelicula().getCodigo());

        Coleccion guardada = coleccionRepo.save(coleccion);

        // TODO: emitir evento de dominio COLECCION_CREADA para reactividad futura (SSE/WebSockets)

        return coleccionMapper.toResponse(guardada);
    }

    @Override
    public ColeccionResponse actualizar(ColeccionRequest request) throws Exception {
        Coleccion coleccion = coleccionMapper.toEntity(request);

        ColeccionCompuesta id = construirId(
                coleccion.getCliente().getCedula(),
                coleccion.getPelicula().getCodigo());

        Optional<Coleccion> buscado = coleccionRepo.findById(id);
        validarExiste(buscado);

        Coleccion actualizada = coleccionRepo.save(coleccion);

        // TODO: emitir evento de dominio COLECCION_ACTUALIZADA para reactividad futura (SSE/WebSockets)

        return coleccionMapper.toResponse(actualizada);
    }

    @Override
    public void eliminar(Integer cedula, Integer codigoPelicula, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);

        ColeccionCompuesta id = construirId(cedula, codigoPelicula);
        Optional<Coleccion> buscado = coleccionRepo.findById(id);
        validarExiste(buscado);
        coleccionRepo.delete(buscado.get());
    }

    @Override
    public Optional<ColeccionResponse> obtener(Integer cedula, Integer codigoPelicula) throws Exception {
        ColeccionCompuesta id = construirId(cedula, codigoPelicula);
        Optional<Coleccion> buscado = coleccionRepo.findById(id);
        validarExiste(buscado);
        return buscado.map(coleccionMapper::toResponse);
    }

    @Override
    public List<ColeccionResponse> listar() {
        return coleccionMapper.toResponseList(coleccionRepo.findAll());
    }

    @Override
    public List<ColeccionResponse> listarPaginado() {
        return coleccionMapper.toResponseList(coleccionRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    @Override
    public List<ColeccionResponse> listarPaginado(Pageable pageable) {
        return coleccionMapper.toResponseList(coleccionRepo.findAll(pageable).toList());
    }

    // !SECTION
    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<ColeccionResponse> listarPorCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        List<Coleccion> colecciones = coleccionRepo.listarPorCliente(cedula);
        validarExiste(colecciones);
        return coleccionMapper.toResponseList(colecciones);
    }

    @Override
    public List<ColeccionResponse> listarPorPelicula(Integer codigoPelicula) throws Exception {
        validarPeliculaExiste(codigoPelicula);
        List<Coleccion> colecciones = coleccionRepo.listarPorPelicula(codigoPelicula);
        validarExiste(colecciones);
        return coleccionMapper.toResponseList(colecciones);
    }

    @Override
    public Double obtenerPuntuacionPromedioPelicula(Integer codigoPelicula) throws Exception {
        validarPeliculaExiste(codigoPelicula);
        Double promedio = coleccionRepo.obtenerPuntuacionPromedio(codigoPelicula);
        if (promedio == null) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_COLLECTION_NOT_FOUND);
        }
        return promedio;
    }

    @Override
    public Long contarColeccionesCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        return coleccionRepo.contarPorCliente(cedula);
    }

    @Override
    public ColeccionResponse calificarPelicula(Integer cedula, Integer codigoPelicula, Double puntuacion) throws Exception {
        Optional<Coleccion> buscado = obtenerEntidad(cedula, codigoPelicula);
        Coleccion coleccion = buscado.get();
        coleccion.setPuntuacion(puntuacion);
        return coleccionMapper.toResponse(coleccionRepo.save(coleccion));
    }

    @Override
    public ColeccionResponse cambiarEstadoPelicula(Integer cedula, Integer codigoPelicula, EstadoPropio estado) throws Exception {
        Optional<Coleccion> buscado = obtenerEntidad(cedula, codigoPelicula);
        Coleccion coleccion = buscado.get();
        coleccion.setEstadoPeliculaPropio(estado);
        return coleccionMapper.toResponse(coleccionRepo.save(coleccion));
    }

    private Optional<Coleccion> obtenerEntidad(Integer cedula, Integer codigoPelicula) throws Exception {
        ColeccionCompuesta id = construirId(cedula, codigoPelicula);
        Optional<Coleccion> buscado = coleccionRepo.findById(id);
        validarExiste(buscado);
        return buscado;
    }
    // !SECTION
}
