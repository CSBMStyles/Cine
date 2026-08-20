package com.unicine.service.movie;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.movie.Comentario;
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.user.Cliente;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.movie.ComentarioRepo;
import com.unicine.repository.movie.PeliculaRepo;
import com.unicine.repository.purchase.EntradaRepo;
import com.unicine.repository.user.ClienteRepo;
import com.unicine.transfer.dto.request.ComentarioRequest;
import com.unicine.transfer.dto.response.ComentarioResponse;
import com.unicine.transfer.mapper.ComentarioMapper;
import com.unicine.util.validation.catalog.domain.MovieErrorCatalog;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;

/**
 * Implementacion del servicio de comentarios y reseñas de peliculas.
 *
 * Gestiona el registro, actualizacion, consulta y reacciones de comentarios,
 * validando que el cliente haya asistido a una funcion de la pelicula.
 */
@Service
@Validated
public class ComentarioServicioImp implements ComentarioServicio {

    private final ComentarioRepo comentarioRepo;
    private final ClienteRepo clienteRepo;
    private final PeliculaRepo peliculaRepo;
    private final EntradaRepo entradaRepo;
    private final ComentarioMapper comentarioMapper;

    public ComentarioServicioImp(ComentarioRepo comentarioRepo, ClienteRepo clienteRepo,
                                 PeliculaRepo peliculaRepo, EntradaRepo entradaRepo,
                                 ComentarioMapper comentarioMapper) {
        this.comentarioRepo = comentarioRepo;
        this.clienteRepo = clienteRepo;
        this.peliculaRepo = peliculaRepo;
        this.entradaRepo = entradaRepo;
        this.comentarioMapper = comentarioMapper;
    }

    // SECTION: Metodos de soporte

    private void validarExiste(Optional<Comentario> comentario) {
        if (comentario.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_COMMENT_NOT_FOUND);
        }
    }

    private void validarExiste(List<Comentario> comentarios) {
        if (comentarios.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_COMMENT_NOT_FOUND);
        }
    }

    private void validarClienteExiste(Integer cedula) {
        Optional<Cliente> cliente = clienteRepo.findById(cedula);
        if (cliente.isEmpty()) {
            throw new ResourceNotFoundException(UserErrorCatalog.DOMAIN_USER_ENTITY_CLIENT_NOT_FOUND);
        }
    }

    private void validarPeliculaExiste(Integer codigo) {
        Optional<Pelicula> pelicula = peliculaRepo.findById(codigo);
        if (pelicula.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_MOVIE_NOT_FOUND);
        }
    }

    private void validarClienteAsistio(Integer cedula, Integer codigoPelicula) {
        boolean tieneEntrada = entradaRepo.clienteTieneEntradaParaPelicula(cedula, codigoPelicula);
        if (!tieneEntrada) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_COMMENT_NOT_ALLOWED_WITHOUT_ATTENDING);
        }
    }

    private void comprobarConfirmacion(boolean confirmacion) throws Exception {
        if (!confirmacion) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_DELETE_DELETE_NOT_CONFIRMED);
        }
    }

    // !SECTION
    // SECTION: Implementacion de servicios Crud

    @Override
    public ComentarioResponse registrar(ComentarioRequest request) throws Exception {
        Comentario comentario = comentarioMapper.toEntity(request);

        validarClienteExiste(comentario.getCliente().getCedula());
        validarPeliculaExiste(comentario.getPelicula().getCodigo());
        validarClienteAsistio(comentario.getCliente().getCedula(), comentario.getPelicula().getCodigo());

        comentario.setFecha(LocalDateTime.now());
        Comentario guardado = comentarioRepo.save(comentario);

        // TODO: emitir evento de dominio COMENTARIO_CREADO para reactividad futura (SSE/WebSockets)

        return comentarioMapper.toResponse(guardado);
    }

    @Override
    public ComentarioResponse actualizar(ComentarioRequest request) throws Exception {
        Comentario comentario = comentarioMapper.toEntity(request);

        Optional<Comentario> buscado = comentarioRepo.findById(comentario.getCodigo());
        validarExiste(buscado);

        validarClienteExiste(comentario.getCliente().getCedula());
        validarPeliculaExiste(comentario.getPelicula().getCodigo());

        Comentario existente = buscado.get();
        existente.setTexto(comentario.getTexto());

        return comentarioMapper.toResponse(comentarioRepo.save(existente));
    }

    @Override
    public void eliminar(Integer codigo, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);

        Optional<Comentario> buscado = comentarioRepo.findById(codigo);
        validarExiste(buscado);
        comentarioRepo.delete(buscado.get());

        // TODO: emitir evento de dominio COMENTARIO_ELIMINADO para reactividad futura (SSE/WebSockets)
    }

    @Override
    public Optional<ComentarioResponse> obtener(Integer codigo) throws Exception {
        Optional<Comentario> buscado = comentarioRepo.findById(codigo);
        validarExiste(buscado);
        return buscado.map(comentarioMapper::toResponse);
    }

    @Override
    public List<ComentarioResponse> listar() {
        return comentarioMapper.toResponseList(comentarioRepo.findAll());
    }

    @Override
    public List<ComentarioResponse> listarPaginado() {
        return comentarioMapper.toResponseList(comentarioRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    // !SECTION
    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<ComentarioResponse> listarPorPelicula(Integer codigoPelicula) throws Exception {
        validarPeliculaExiste(codigoPelicula);
        List<Comentario> comentarios = comentarioRepo.findByPeliculaCodigo(codigoPelicula);
        validarExiste(comentarios);
        return comentarioMapper.toResponseList(comentarios);
    }

    @Override
    public List<ComentarioResponse> listarPorCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        List<Comentario> comentarios = comentarioRepo.findByClienteCedula(cedula);
        validarExiste(comentarios);
        return comentarioMapper.toResponseList(comentarios);
    }

    @Override
    public ComentarioResponse darLike(Integer codigo) throws Exception {
        Optional<Comentario> buscado = comentarioRepo.findById(codigo);
        validarExiste(buscado);

        Comentario comentario = buscado.get();
        comentario.setLikes(comentario.getLikes() + 1);
        Comentario actualizado = comentarioRepo.save(comentario);

        // TODO: emitir evento de dominio COMENTARIO_LIKE para reactividad futura (SSE/WebSockets)

        return comentarioMapper.toResponse(actualizado);
    }

    @Override
    public ComentarioResponse darDislike(Integer codigo) throws Exception {
        Optional<Comentario> buscado = comentarioRepo.findById(codigo);
        validarExiste(buscado);

        Comentario comentario = buscado.get();
        comentario.setDislikes(comentario.getDislikes() + 1);
        Comentario actualizado = comentarioRepo.save(comentario);

        // TODO: emitir evento de dominio COMENTARIO_DISLIKE para reactividad futura (SSE/WebSockets)

        return comentarioMapper.toResponse(actualizado);
    }
    // !SECTION
}
