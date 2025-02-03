package com.unicine.servicio;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.gson.Gson;
import com.unicine.entidades.Funcion;
import com.unicine.entidades.FuncionEsquema;
import com.unicine.repo.DistribucionSillaRepo;
import com.unicine.repo.FuncionEsquemaRepo;

import jakarta.validation.Valid;

@Service
@Validated
public class FuncionEsquemaServicioImp implements FuncionEsquemaServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final FuncionEsquemaRepo funcionEsquemaRepo;

    private final DistribucionSillaRepo distribucionSillaRepo;

    public FuncionEsquemaServicioImp(FuncionEsquemaRepo funcionEsquemaRepo, DistribucionSillaRepo distribucionSillaRepo) {
        this.funcionEsquemaRepo = funcionEsquemaRepo;
        this.distribucionSillaRepo = distribucionSillaRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia de la disposicion que se esta buscando
     * @param numero
     * @throws
     */
    private void validarExiste(Optional<FuncionEsquema> funcionEsquema) throws Exception {

        if (funcionEsquema.isEmpty()) {
            throw new Exception("La disposicion de pelicula no existe");
        }
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

   /**
    * Metodo para obtener la funcion de sillas de una funcion
    * @param funcion
    * @return
    */
   private String obtenerEsquemaFuncion(Funcion funcion) {

        return distribucionSillaRepo.obtenerDistribucionFuncion(funcion.getCodigo());
    }

    private void reemplazarDatos(String esquema, FuncionEsquema funcionEsquema) {
        // Crear instancia de gson para manejar conversiones JSON
        Gson gson = new Gson();
        
        // Convertir el string JSON a una matriz bidimensional de Strings
        String[][] matriz = gson.fromJson(esquema, String[][].class);
    
        // Iniciar conteo usando streams para procesar la matriz
        Map<String, Integer> conteo = Arrays.stream(matriz)         // Convierte la matriz en stream
                .flatMap(Arrays::stream)                            // Aplana la matriz a un arreglo
                .filter(s -> s != null && !s.trim().isEmpty())      // Filtra valores nulos y vacíos
                .collect(Collectors.groupingBy(                     // Agrupa los elementos
                        Function.identity(),                        // Usa el valor como clave
                        Collectors.collectingAndThen(               // Transforma el resultado
                            Collectors.counting(),              // Cuenta ocurrencias
                            Long::intValue)));                  // Conversion de datos
    
        // Actualiza los contadores en el objeto funcionEsquema
        funcionEsquema.setMantenimiento(conteo.getOrDefault("M", 0));
        funcionEsquema.setDisponibles(conteo.getOrDefault("D", 0));
        funcionEsquema.setOcupadas(conteo.getOrDefault("O", 0));
        
        funcionEsquema.setEsquemaTemporal(esquema);
    }

    // SECTION: Implementacion de servicios

    // 1️⃣ Funcion del Administrador

    @Override
    public FuncionEsquema registrar(@Valid FuncionEsquema funcionEsquema) throws Exception { 
        
        reemplazarDatos(obtenerEsquemaFuncion(funcionEsquema.getFuncion()), funcionEsquema);

        return funcionEsquemaRepo.save(funcionEsquema); 
    }

    @Override
    public FuncionEsquema actualizar(@Valid FuncionEsquema funcionEsquema) throws Exception { return funcionEsquemaRepo.save(funcionEsquema); }

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

    @Override
    public List<FuncionEsquema> listarPaginado() { 

        return funcionEsquemaRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    @Override
    public List<FuncionEsquema> listarAscendente() { 
        
        return funcionEsquemaRepo.findAll(Sort.by("codigo").ascending());
    }

    @Override
    public List<FuncionEsquema> listarDescendente() { 
        
        return funcionEsquemaRepo.findAll(Sort.by("codigo").descending());
    }
}
