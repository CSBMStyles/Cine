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

import com.unicine.entity.Imagen;
import com.unicine.entity.Persona;
import com.unicine.entity.interfaced.Imagenable;
import com.unicine.repository.ImagenRepo;
import com.unicine.service.extend.image.ImageKitService;
import com.unicine.util.funtional.image.RefactorizadorRuta;

import io.imagekit.sdk.models.results.Result;
import jakarta.validation.Valid;

@Service
@Validated
public class ImagenServicioImp implements ImagenServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final ImagenRepo imagenRepo;

    @Autowired
    private ImageKitService imageKitService;

    @Autowired
    private RefactorizadorRuta refactorizadorRuta;

    public ImagenServicioImp(ImagenRepo imagenRepo) {
        this.imagenRepo = imagenRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia de la disposicion que se esta buscando
     * @param numero
     * @throws
     */
    private void validarExiste(Optional<Imagen> imagen) throws Exception {

        if (imagen.isEmpty()) {
            throw new Exception("La imagen no existe");
        }
    }

    private void validarExiste(Imagenable propietario) throws Exception {

        if (propietario instanceof Persona) {
            
            Persona persona = (Persona) propietario;

            Optional<Imagen> imagenRelacion = imagenRepo.findByPersona(persona.getCedula());
            
            if (imagenRelacion.isPresent()) {
                throw new Exception("La persona ya tiene una imagen, deberia utilizar el metodo actualizar");
            }
        }  
    }

    /**
     * Metodo para construir la carpeta de la imagen
     * @param propietario
     * @return
     */
    private String constructorCarpeta(Imagenable propietario) {

        if (propietario instanceof Persona) {

            return "personas" + "/" + propietario.getCarpetaPrefijo();

        } else {

            String nombreEntidad = refactorizadorRuta.remplazarDenominacion(propietario.getNombre());
    
            return propietario.getCarpetaPrefijo() + "/" + nombreEntidad;
        }
    }

    /**
     * Método para reasignar los datos de la imagen
     * @param pelicula
     * @param result
     */
    private void reasignarDatos(Imagen imagen, Result result) {

        // Extraer la respuesta en forma de mapa
        Map<String, Object> respuesta = result.getResponseMetaData().getMap();

        System.out.println("Respuesta de la peticion" + respuesta);

        String fileId = respuesta.get("fileId").toString();

        String urlImagen = respuesta.get("url").toString();

        // Actualizar la imagen
        imagen.setCodigo(fileId);

        imagen.setUrl(urlImagen);
    }

    /**
     * Metodo para comprobar la presencia de las relaciones del imagen
     * @param imagen
     * @throws
     */
    private void comprobarConfirmacion(boolean confirmacion) {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

    // SECTION: Implementacion de servicios

    // 1️⃣ Funcion del Administrador

    @Override
    public Imagen registrar(@Valid Imagen imagen, File file, Imagenable propietario) throws Exception { 

        validarExiste(propietario);

        String folder = constructorCarpeta(propietario);

        Result result = imageKitService.subirImagen(file, folder, propietario, false, null);

        reasignarDatos(imagen, result);

        return imagenRepo.save(imagen); 
    }

    @Override
    public Imagen actualizar(@Valid Imagen imagen, File file, Imagenable propietario) throws Exception { 
        
        String folder = constructorCarpeta(propietario);

        Result result = imageKitService.actualizarImagen(file, imagen.getCodigo(), folder, propietario);

        reasignarDatos(imagen, result);

        return imagenRepo.save(imagen); 
    }

    @Override
    public Imagen renombrar(@Valid Imagen imagen, String nuevoNombre, Imagenable propietario) throws Exception {

        Result result = imageKitService.renombrarImagen(imagen.getCodigo(), nuevoNombre, propietario);

        reasignarDatos(imagen, result);

        return imagenRepo.save(imagen);
    }


    // Implementar de aqui en adelante

    @Override
    public void eliminar(@Valid Imagen eliminado, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        imagenRepo.delete(eliminado);
    }

    // REVIEW: En este caso se utiliza una clase de validacion para obtener el codigo de la imagen usando las anotaciones para validar

    @Override
    public Optional<Imagen> obtener(String codigo) throws Exception {

        Optional<Imagen> buscado = imagenRepo.findById(codigo);

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<Imagen> listar() { return imagenRepo.findAll(); }

    @Override
    public List<Imagen> listarPaginado() { 

        return imagenRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    @Override
    public List<Imagen> listarAscendente() { 
        
        return imagenRepo.findAll(Sort.by("codigo").ascending());
    }

    @Override
    public List<Imagen> listarDescendente() { 
        
        return imagenRepo.findAll(Sort.by("codigo").descending());
    }
}
