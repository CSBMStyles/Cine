package com.unicine.servicio;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entidades.DistribucionSilla;
import com.unicine.repo.DistribucionSillaRepo;
import com.unicine.util.validacion.atributos.DistribucionAtributoValidator;

import jakarta.validation.Valid;
import com.google.gson.Gson;

@Service
@Validated
public class DistribucionSillaServicioImp implements DistribucionSillaServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final DistribucionSillaRepo distribucionRepo;

    public DistribucionSillaServicioImp(DistribucionSillaRepo distribucionRepo) {
        this.distribucionRepo = distribucionRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia del distribucion que se esta buscando
     * @param numero
     * @throws
     */
    private void validarExiste(Optional<DistribucionSilla> distribucion) throws Exception {

        if (distribucion.isEmpty()) {
            throw new Exception("La distribucion de sillas no existe");
        }
    }

    /**
     * Metodo para comprobar la presencia de las relaciones del distribucion
     * @param distribucion
     * @throws
     */
    private void comprobarConfirmacion(boolean confirmacion) {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

   /**
    * Metodo para reemplazar los datos de la distribucion
    * @param distribucion
    * @param matriz
    */
    private void reemplazarDatos(DistribucionSilla distribucion) {

        Gson gson = new Gson();
        String[][] matriz = gson.fromJson(distribucion.getEsquema(), String[][].class);

        // Contar filas y columnas
        int filas = matriz.length;
        int columnas = (filas > 0) ? matriz[0].length : 0;

        // Contar el total de sillas que no son espacios vacíos usando programación
        // funcional
        int totalSillas = (int) Arrays.stream(matriz)
                .flatMap(Arrays::stream)    // Convertir matriz a un arreglo de una dimensión
                .filter(silla -> silla != null && !silla.trim().isEmpty()) // Filtrar sillas no vacías
                .count();   // Contar sillas no vacías

        // Setear filas, columnas y total de sillas en la entidad DistribucionSilla
        distribucion.setFilas(filas);
        distribucion.setColumnas(columnas);
        distribucion.setTotalSillas(totalSillas);
    }

   /**
    * Metodo para transformar un String a un Integer
    * @param codigo
    * @return
    */
   private Integer transformar(String codigo) { return Integer.parseInt(codigo); }

    // SECTION: Implementacion de servicios

    // 1️⃣2️⃣ Funciones del Administradores

    @Override
    public DistribucionSilla registrar(@Valid DistribucionSilla distribucion) throws Exception {

        reemplazarDatos(distribucion);

        return distribucionRepo.save(distribucion);
    }

    @Override
    public DistribucionSilla actualizar(@Valid DistribucionSilla distribucion) throws Exception {

        reemplazarDatos(distribucion);
        
        return distribucionRepo.save(distribucion);
    }

    @Override
    public void eliminar(@Valid DistribucionSilla eliminado, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        distribucionRepo.delete(eliminado);
    }

    // REVIEW: En este caso se utiliza una clase de validacion para obtener el codigo de la distribucion usando las anotaciones para validar

    @Override
    public Optional<DistribucionSilla> obtener(@Valid DistribucionAtributoValidator validacion) throws Exception {

        Optional<DistribucionSilla> buscado = distribucionRepo.findById(transformar(validacion.getCodigo()));

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<DistribucionSilla> listar() { return distribucionRepo.findAll(); }

    @Override
    public List<DistribucionSilla> listarPaginado() { 

        return distribucionRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    @Override
    public List<DistribucionSilla> listarAscendente() { 
        
        return distribucionRepo.findAll(Sort.by("codigo").ascending());
    }

    @Override
    public List<DistribucionSilla> listarDescendente() { 
        
        return distribucionRepo.findAll(Sort.by("codigo").descending());
    }
}
