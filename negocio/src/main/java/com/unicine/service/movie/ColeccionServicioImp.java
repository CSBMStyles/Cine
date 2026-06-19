package com.unicine.service.movie;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
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
import com.unicine.util.validation.catalog.domain.MovieErrorCatalog;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;

import jakarta.validation.Valid;

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

    public ColeccionServicioImp(ColeccionRepo coleccionRepo, ClienteRepo clienteRepo, PeliculaRepo peliculaRepo) {
        this.coleccionRepo = coleccionRepo;
        this.clienteRepo = clienteRepo;
        this.peliculaRepo = peliculaRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia de la coleccion que se esta buscando.
     * Lanza ResourceNotFoundException si no se encuentra.
     */
    private void validarExiste(Optional<Coleccion> coleccion) throws Exception {
        if (coleccion.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_COLLECTION_NOT_FOUND);
        }
    }

    /**
     * Metodo para comprobar que la lista de colecciones no este vacia.
     * Lanza ResourceNotFoundException si la lista esta vacia.
     */
    private void validarExiste(List<Coleccion> colecciones) throws Exception {
        if (colecciones.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_COLLECTION_NOT_FOUND);
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
     * Valida que la pelicula exista en la base de datos.
     */
    private void validarPeliculaExiste(Integer codigo) throws Exception {
        Optional<Pelicula> pelicula = peliculaRepo.findById(codigo);
        if (pelicula.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_MOVIE_NOT_FOUND);
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
    public Coleccion registrar(@Valid Coleccion coleccion) throws Exception {
        validarClienteExiste(coleccion.getCliente().getCedula());
        validarPeliculaExiste(coleccion.getPelicula().getCodigo());

        Coleccion guardada = coleccionRepo.save(coleccion);

        // TODO: emitir evento de dominio COLECCION_CREADA para reactividad futura (SSE/WebSockets)

        return guardada;
    }

    @Override
    public Coleccion actualizar(@Valid Coleccion coleccion) throws Exception {
        ColeccionCompuesta id = new ColeccionCompuesta(
                coleccion.getCliente().getCedula(),
                coleccion.getPelicula().getCodigo());
        Optional<Coleccion> buscado = coleccionRepo.findById(id);
        validarExiste(buscado);

        Coleccion actualizada = coleccionRepo.save(coleccion);

        // TODO: emitir evento de dominio COLECCION_ACTUALIZADA para reactividad futura (SSE/WebSockets)

        return actualizada;
    }

    @Override
    public void eliminar(@Valid Coleccion coleccion, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);
        coleccionRepo.delete(coleccion);
    }

    @Override
    public Optional<Coleccion> obtener(Integer cedula, Integer codigoPelicula) throws Exception {
        ColeccionCompuesta id = new ColeccionCompuesta(cedula, codigoPelicula);
        Optional<Coleccion> buscado = coleccionRepo.findById(id);
        validarExiste(buscado);
        return buscado;
    }

    @Override
    public List<Coleccion> listar() {
        return coleccionRepo.findAll();
    }

    @Override
    public List<Coleccion> listarPaginado() {
        return coleccionRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<Coleccion> listarPorCliente(Integer cedula) throws Exception {
        validarClienteExiste(cedula);
        List<Coleccion> colecciones = coleccionRepo.listarPorCliente(cedula);
        validarExiste(colecciones);
        return colecciones;
    }

    @Override
    public List<Coleccion> listarPorPelicula(Integer codigoPelicula) throws Exception {
        validarPeliculaExiste(codigoPelicula);
        List<Coleccion> colecciones = coleccionRepo.listarPorPelicula(codigoPelicula);
        validarExiste(colecciones);
        return colecciones;
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
    public Coleccion calificarPelicula(Integer cedula, Integer codigoPelicula, Double puntuacion) throws Exception {
        Optional<Coleccion> buscado = obtener(cedula, codigoPelicula);
        Coleccion coleccion = buscado.get();
        coleccion.setPuntuacion(puntuacion);
        return coleccionRepo.save(coleccion);
    }

    @Override
    public Coleccion cambiarEstadoPelicula(Integer cedula, Integer codigoPelicula, EstadoPropio estado) throws Exception {
        Optional<Coleccion> buscado = obtener(cedula, codigoPelicula);
        Coleccion coleccion = buscado.get();
        coleccion.setEstadoPeliculaPropio(estado);
        return coleccionRepo.save(coleccion);
    }
}
