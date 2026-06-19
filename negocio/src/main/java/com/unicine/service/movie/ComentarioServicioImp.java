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
import com.unicine.util.validation.catalog.domain.MovieErrorCatalog;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;

import jakarta.validation.Valid;

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

    public ComentarioServicioImp(ComentarioRepo comentarioRepo, ClienteRepo clienteRepo,
                                 PeliculaRepo peliculaRepo, EntradaRepo entradaRepo) {
        this.comentarioRepo = comentarioRepo;
        this.clienteRepo = clienteRepo;
        this.peliculaRepo = peliculaRepo;
        this.entradaRepo = entradaRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia del comentario que se esta buscando.
     * Lanza ResourceNotFoundException si no se encuentra.
     */
    private void validarExiste(Optional<Comentario> comentario) {
        if (comentario.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_COMMENT_NOT_FOUND);
        }
    }

    /**
     * Metodo para comprobar que la lista de comentarios no este vacia.
     * Lanza ResourceNotFoundException si la lista esta vacia.
     */
    private void validarExiste(List<Comentario> comentarios) {
        if (comentarios.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_COMMENT_NOT_FOUND);
        }
    }

    /**
     * Valida que el cliente exista en la base de datos.
     */
    private void validarClienteExiste(Integer cedula) {
        Optional<Cliente> cliente = clienteRepo.findById(cedula);
        if (cliente.isEmpty()) {
            throw new ResourceNotFoundException(UserErrorCatalog.DOMAIN_USER_ENTITY_CLIENT_NOT_FOUND);
        }
    }

    /**
     * Valida que la pelicula exista en la base de datos.
     */
    private void validarPeliculaExiste(Integer codigo) {
        Optional<Pelicula> pelicula = peliculaRepo.findById(codigo);
        if (pelicula.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_MOVIE_NOT_FOUND);
        }
    }

    /**
     * Valida que el cliente tenga al menos una entrada para una funcion de la pelicula.
     * NOTA: Se asume que comprar entrada implica asistencia. En el futuro se validara asistencia real.
     */
    private void validarClienteAsistio(Integer cedula, Integer codigoPelicula) {
        boolean tieneEntrada = entradaRepo.clienteTieneEntradaParaPelicula(cedula, codigoPelicula);
        if (!tieneEntrada) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_COMMENT_NOT_ALLOWED_WITHOUT_ATTENDING);
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

    // SECTION: Implementacion de servicios CRUD

    @Override
    public Comentario registrar(@Valid Comentario comentario) throws Exception {
        validarClienteExiste(comentario.getCliente().getCedula());
        validarPeliculaExiste(comentario.getPelicula().getCodigo());
        validarClienteAsistio(comentario.getCliente().getCedula(), comentario.getPelicula().getCodigo());

        comentario.setFecha(LocalDateTime.now());
        Comentario guardado = comentarioRepo.save(comentario);

        // TODO: emitir evento de dominio COMENTARIO_CREADO para reactividad futura (SSE/WebSockets)

        return guardado;
    }

    @Override
    public Comentario actualizar(@Valid Comentario comentario) throws Exception {
        Optional<Comentario> buscado = comentarioRepo.findById(comentario.getCodigo());
        validarExiste(buscado);

        validarClienteExiste(comentario.getCliente().getCedula());
        validarPeliculaExiste(comentario.getPelicula().getCodigo());

        Comentario existente = buscado.get();
        existente.setTexto(comentario.getTexto());

        return comentarioRepo.save(existente);
    }

    @Override
    public void eliminar(@Valid Comentario comentario, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);
        comentarioRepo.delete(comentario);

        // TODO: emitir evento de dominio COMENTARIO_ELIMINADO para reactividad futura (SSE/WebSockets)
    }

    @Override
    public Optional<Comentario> obtener(Integer codigo) throws Exception {
        Optional<Comentario> buscado = comentarioRepo.findById(codigo);
        validarExiste(buscado);
        return buscado;
    }

    @Override
    public List<Comentario> listar() {
        return comentarioRepo.findAll();
    }

    @Override
    public List<Comentario> listarPaginado() {
        return comentarioRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<Comentario> listarPorPelicula(Integer codigoPelicula) throws Exception {
        validarPeliculaExiste(codigoPelicula);
        List<Comentario> comentarios = comentarioRepo.findByPeliculaCodigo(codigoPelicula);
        validarExiste(comentarios);
        return comentarios;
    }

    @Override
    public List<Comentario> listarPorCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        List<Comentario> comentarios = comentarioRepo.findByClienteCedula(cedula);
        validarExiste(comentarios);
        return comentarios;
    }

    @Override
    public Comentario darLike(Integer codigo) throws Exception {
        Optional<Comentario> buscado = comentarioRepo.findById(codigo);
        validarExiste(buscado);

        Comentario comentario = buscado.get();
        comentario.setLikes(comentario.getLikes() + 1);
        Comentario actualizado = comentarioRepo.save(comentario);

        // TODO: emitir evento de dominio COMENTARIO_LIKE para reactividad futura (SSE/WebSockets)

        return actualizado;
    }

    @Override
    public Comentario darDislike(Integer codigo) throws Exception {
        Optional<Comentario> buscado = comentarioRepo.findById(codigo);
        validarExiste(buscado);

        Comentario comentario = buscado.get();
        comentario.setDislikes(comentario.getDislikes() + 1);
        Comentario actualizado = comentarioRepo.save(comentario);

        // TODO: emitir evento de dominio COMENTARIO_DISLIKE para reactividad futura (SSE/WebSockets)

        return actualizado;
    }
}
