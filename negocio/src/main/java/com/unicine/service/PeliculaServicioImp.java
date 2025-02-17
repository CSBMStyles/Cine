package com.unicine.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.Pelicula;
import com.unicine.repository.PeliculaRepo;
import com.unicine.service.extend.image.ImageKitService;
import com.unicine.util.validaciones.atributos.PeliculaAtributoValidator;

import io.imagekit.sdk.models.results.Result;
import jakarta.validation.Valid;

@Service
@Validated
public class PeliculaServicioImp implements PeliculaServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final PeliculaRepo peliculaRepo;

    @Autowired
    private ImageKitService imageKitService;


    public PeliculaServicioImp(PeliculaRepo peliculaRepo) {
        this.peliculaRepo = peliculaRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia del pelicula que se esta buscando
     * @param pelicula
     */
    private void validarExiste(Optional<Pelicula> pelicula) throws Exception {

        if (pelicula.isEmpty()) {
            throw new Exception("La pelicula no existe");
        }
    }

    /**
     * Metodo para comprobar la presencia la lista de peliculas que se esta buscando
     * @param pelicula
     */
    private void validarExiste(List<Pelicula> pelicula) throws Exception {

        if (pelicula.isEmpty()) {
            throw new Exception("No existe peliculas con ese nombre");
        }
    }

    /**
     * Método que verifica si existe un pelicula con el mismo nombre
     * @param pelicula
     */
    private void validarExisteNombre(Pelicula pelicula) throws Exception {

        Optional<Pelicula> existe = peliculaRepo.obtenerPeliculaNombre(pelicula.getNombre());
       
        if (existe.isPresent()) {
            throw new RuntimeException("La pelicula ya existe");
        }
    }

    /**
     * Método que verifica si existe un pelicula con el mismo nombre adicional se excluye el pelicula que se esta actualizando
     * @param pelicula
     */
    private void validarRepiteNombre(Pelicula pelicula) throws Exception {

        Optional<Pelicula> existe = peliculaRepo.obtenerNombreExcluido(pelicula.getNombre(), pelicula.getCodigo());
       
        if (existe.isPresent()) {
            throw new RuntimeException("El nombre que esta ingresando ya existe");
        }
    }

    /**
     * Método para reasignar los datos de la imagen
     * @param pelicula
     * @param result
     */
    private void reasignarDatos(Pelicula pelicula, Result result) {

        // Extraer la respuesta en forma de mapa
        Map<String, Object> respuesta = result.getResponseMetaData().getMap();

        String publicId = (String) respuesta.get("fileId");

        String urlImagen = (String) respuesta.get("url");

        // Actualizar la entidad
        pelicula.getImagenes().put(publicId, urlImagen);
    }

    /**
     * Método para subir una imagen a imagekit e ingresarle los valores de la imagen guardada
     * @param pelicula
     * @param imagen
     */
    private void subirImagen(Pelicula pelicula, File imagen) throws Exception {
 
        // Subir la imagen a imagekit
        Result result = imageKitService.subirImagen(imagen, "peliculas/" + pelicula.getNombre());

        reasignarDatos(pelicula, result);
    }


    private void actualizarImagen(Pelicula pelicula, File imagen, String imagenAntiguo) throws Exception {
        
        if (imagen != null) {
            // Subir la imagen a imagekit
            Result result = imageKitService.actualizarImagen(imagen, imagenAntiguo, "peliculas/" + pelicula.getNombre());

            reasignarDatos(pelicula, result);
        }
    }

    /**
     * Metodo para validar la confirmacion de la eliminacion
     * @param confirmacion
     */
    private void comprobarConfirmacion(boolean confirmacion) throws Exception {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

    // SECTION: Implementacion de servicios

    // 1️⃣ Funciones del Administrador

    @Override
    public Pelicula registrar(@Valid Pelicula pelicula, File imagen) throws Exception { 

        validarExisteNombre(pelicula);
        subirImagen(pelicula, imagen);

        return peliculaRepo.save(pelicula);
    }

    @Override
    public Pelicula actualizar(@Valid Pelicula pelicula, File imagen, String imagenAntiguo) throws Exception {

        validarRepiteNombre(pelicula);
        actualizarImagen(pelicula, imagen, imagenAntiguo);

        return peliculaRepo.save(pelicula);
    }

    @Override
    public void eliminar(@Valid Pelicula eliminado, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        peliculaRepo.delete(eliminado);
    }

    // *️⃣ Funciones Generales

    @Override
    public Optional<Pelicula> obtener(@Valid PeliculaAtributoValidator validator) throws Exception {

        Optional<Pelicula> buscado = peliculaRepo.findById(validator.getCodigo());

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<Pelicula> obtenerNombrePeliculas(@Valid PeliculaAtributoValidator validator) throws Exception {

        List<Pelicula> peliculas = peliculaRepo.buscarNombres(validator.getNombre());

        validarExiste(peliculas);

        return peliculas; 
    }


    @Override
    public List<Pelicula> listar() { return peliculaRepo.findAll(); }

    @Override
    public List<Pelicula> listarPaginado() { 

        return peliculaRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    @Override
    public List<Pelicula> listarAscendente() { 
        
        return peliculaRepo.findAll(Sort.by("codigo").ascending());
    }

    @Override
    public List<Pelicula> listarDescendente() { 
        
        return peliculaRepo.findAll(Sort.by("codigo").descending());
    }
}
