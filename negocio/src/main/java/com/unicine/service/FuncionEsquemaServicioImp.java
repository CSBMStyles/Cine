package com.unicine.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.gson.Gson;
import com.unicine.entity.Funcion;
import com.unicine.entity.FuncionEsquema;
import com.unicine.repository.DistribucionSillaRepo;
import com.unicine.repository.FuncionEsquemaRepo;

import jakarta.validation.Valid;

@Service
@Validated
public class FuncionEsquemaServicioImp implements FuncionEsquemaServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final FuncionEsquemaRepo funcionEsquemaRepo;

    private final DistribucionSillaRepo distribucionRepo;

    private final Gson gson = new Gson();

    public FuncionEsquemaServicioImp(FuncionEsquemaRepo funcionEsquemaRepo, DistribucionSillaRepo distribucionRepo) {
        this.funcionEsquemaRepo = funcionEsquemaRepo;
        this.distribucionRepo = distribucionRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia de la disposicion que se esta buscando
     * @param numero
     * @throws
     */
    private void validarExiste(Optional<FuncionEsquema> funcionEsquema) throws Exception {

        if (funcionEsquema.isEmpty()) {
            throw new Exception("El esquema de la funcion no existe");
        }
    }

    /**
     * Metodo para obtener la funcion de sillas de una funcion
     * 
     * @param funcion
     * @return
     */
    private String obtenerEsquema(Funcion funcion) {

        return distribucionRepo.obtenerEsquemaFuncion(funcion.getCodigo());
    }

    /**
     * Metodo para reemplazar los datos de la disposicion de la funcion
     * 
     * @param esquema
     * @param funcionEsquema
     */
    private void reemplazarDatos(String esquema, FuncionEsquema funcionEsquema) {

        // Convertir el string JSON a una matriz bidimensional de Strings
        String[][] matriz = gson.fromJson(esquema, String[][].class);

        // Iniciar conteo usando streams para procesar la matriz
        Map<String, Integer> conteo = Arrays.stream(matriz)    // Convierte la matriz en stream
                .flatMap(Arrays::stream)                       // Aplana la matriz a un arreglo
                .filter(s -> s != null && !s.trim().isEmpty()) // Filtra valores nulos y vacíos
                .collect(Collectors.groupingBy(                // Agrupa los elementos
                        Function.identity(),                   // Usa el valor como clave
                        Collectors.collectingAndThen(          // Transforma el resultado
                        Collectors.counting(),                 // Cuenta ocurrencias
                        Long::intValue)));                     // Conversion de datos

        // Asinar el conteo de las sillas en mantenimiento de la funcion
        funcionEsquema.setMantenimiento(conteo.getOrDefault("M", 0));
        // Asignar el conteo de las sillas disponibles de la funcion
        funcionEsquema.setDisponibles(conteo.getOrDefault("D", 0));
        // Asignar el conteo de las sillas ocupadas de la funcion
        funcionEsquema.setOcupadas(conteo.getOrDefault("O", 0));
        // Asignar el esquema temporal de la funcion
        funcionEsquema.setEsquemaTemporal(gson.toJson(matriz));
    }

    /**
     * Metodo para comprobar la presencia de las relaciones del funcionEsquema
     * @param funcionEsquema
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
    public FuncionEsquema registrar(@Valid FuncionEsquema funcionEsquema) throws Exception { 
        
        reemplazarDatos(obtenerEsquema(funcionEsquema.getFuncion()), funcionEsquema);

        return funcionEsquemaRepo.save(funcionEsquema); 
    }

    @Override
    public FuncionEsquema actualizar(@Valid FuncionEsquema funcionEsquema) throws Exception { 
        
        reemplazarDatos(funcionEsquema.getEsquemaTemporal(), funcionEsquema);

        return funcionEsquemaRepo.save(funcionEsquema); 
    }

    @Override
    public void eliminar(@Valid FuncionEsquema eliminado, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        funcionEsquemaRepo.delete(eliminado);
    }

    // REVIEW: En este caso se utiliza una clase de validacion para obtener el codigo de la funcionEsquema usando las anotaciones para validar

    @Override
    public Optional<FuncionEsquema> obtener(Integer codigo) throws Exception {

        Optional<FuncionEsquema> buscado = funcionEsquemaRepo.findById(codigo);

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<FuncionEsquema> listar() { return funcionEsquemaRepo.findAll(); }
}
