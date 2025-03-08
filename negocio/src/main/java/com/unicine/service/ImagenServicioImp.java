package com.unicine.service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.unicine.transfer.record.VersionArchivo;
import com.unicine.util.funtional.image.RefactorizadorRuta;

import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.models.results.ResultFileVersionDetails;
import io.imagekit.sdk.models.results.ResultFileVersions;
import io.imagekit.sdk.models.results.ResultList;
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

        String fileId = result.getFileId();

        String urlImagen = result.getUrl();

        // REVIEW: Recordar borrar esta infomacion

        System.out.println("Version: " + result.getVersionInfo().getAsJsonObject().get("id"));

        System.out.println("Actualizado: " + result.getUpdatedAt());

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

   /**
     * Metodo para obtener los ids de las imagenes eliminadas
     * @param eliminados
     * @return
     */
   private List<String> obtenerEliminadosId(List<Imagen> eliminados) {

        return eliminados.stream().map(Imagen::getCodigo).collect(Collectors.toList());
    }

    /**
     * Metodo para obtener los ids de las imagenes de la carpeta
     * @param result
     * @return identificadores de las imagenes
     */
    private List<String> obtenerImagenesCarpetaId(ResultList result) {

        return result.getResults().stream().map(response -> response.getFileId()).collect(Collectors.toList());
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
    public Imagen restaurar(@Valid Imagen imagen, String versionId) throws Exception {

        Result result = imageKitService.restaurarVersion(imagen.getCodigo(), versionId);

        reasignarDatos(imagen, result);
        
        return imagenRepo.save(imagen);
    }

    @Override
    public Imagen renombrar(@Valid Imagen imagen, String nuevoNombre, Imagenable propietario) throws Exception {

        Result result = imageKitService.renombrarImagen(imagen.getCodigo(), nuevoNombre, propietario);

        reasignarDatos(imagen, result);

        return imagenRepo.save(imagen);
    }

    @Override
    public void eliminar(@Valid Imagen eliminado, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        imageKitService.eliminarImagen(eliminado.getCodigo());

        imagenRepo.delete(eliminado);
    }

    @Override
    public void eliminarMultiple (@Valid List<Imagen> eliminados, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);
        
        imageKitService.eliminarImagenes(obtenerEliminadosId(eliminados));

        imagenRepo.deleteAll(eliminados);
    }

    // REVIEW: En este caso se utiliza una clase de validacion para obtener el codigo de la imagen usando las anotaciones para validar

    @Override
    public Optional<Imagen> obtener(String codigo) throws Exception {

        Optional<Imagen> buscado = imagenRepo.findById(codigo);

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<String> listar(@Valid Imagenable propietario) throws Exception {

        String folder = constructorCarpeta(propietario);

        ResultList result = imageKitService.listarImagenes(folder);

        return obtenerImagenesCarpetaId(result);
    }

    @Override
    public List<VersionArchivo> listarVersiones(String fileId) throws Exception {

        ResultFileVersions result = imageKitService.listarVersiones(fileId);

        System.out.println("Respuesta detallada de versiones:" + result);

        List<ResultFileVersionDetails> listaRespuesta = result.getResultFileVersionDetailsList();

        // Convierte una lista de respuestas de versiones de archivo en una lista de objetos VersionArchivo.
        List<VersionArchivo> listaVersiones = listaRespuesta.stream().map(version -> new VersionArchivo
        (version.getFileId(), version.getUrl(), version.getUpdatedAt())) // Mapea cada versión recuperada extrayendo su fileId, URL y fecha de actualización
        .collect(Collectors.toList()); // Convierte una lista de respuestas de versiones de archivo en una lista de objetos VersionArchivo.

        return listaVersiones;
    }
    

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
