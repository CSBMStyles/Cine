package com.unicine.servicio;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entidades.Cliente;
import com.unicine.repo.ClienteRepo;

import jakarta.validation.Valid;

@Service
@Validated
public class ClienteServicioImp implements PersonaServicio<Cliente> {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final ClienteRepo clienteRepo;

    public ClienteServicioImp(ClienteRepo clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la autenticacion de un cliente
     * @param numero
     * @throws
     */
    private Cliente comprobarAutenticacion(String correo, String password) throws Exception {

        Optional<Cliente> cliente = clienteRepo.comprobarAutenticacion(correo, password);

        if (cliente.isEmpty()) {
            throw new Exception("Los datos de autenticación son incorrectos");
        }

        return cliente.get();
    }

    /**
     * Metodo para comprobar la presencia del cliente que se esta buscando
     * @param numero
     * @throws
     */
    private void validarExiste(Optional<Cliente> cliente) throws Exception {

        if (cliente.isEmpty()) {
            throw new Exception("El cliente no existe");
        }
    }

    /**
     * Metodo para comprobar la cantidad de digitos de una cedula
     * @param numero
     * @throws
     */
    private void validarFormatoCedula(Integer numero) throws Exception {

        int longitud = numero.toString().length();

        if (numero < 0) {
            throw new Exception("La cédula debe ser un número positivo");
        }
        if (longitud < 7) {
            throw new Exception("La cedula no puede tener menos de siete digitos"); 
        }
        if (longitud > 10) {
            throw new Exception("La cedula no puede tener mas de diez digitos");
        }
    }

    /**
     * Metodo para comprobar la presencia de una cedula en la base de datos
     * @param numero
     * @throws
     */
    private void validarExisteCedula(Integer numero) throws Exception {

        Optional<Cliente> existe = clienteRepo.findById(numero);
        
        if (existe.isPresent()) {
            throw new Exception("Esta cedula ya esta registrada");
        }
    }

    /**
     * Método que verifica si el correo ya esta registrado
     * @param correo
     * @return si existe el correo devuelve true, de lo contrario false
     */
    private void validarExisteCorreo(String correo) {

        Optional<Cliente> existe = clienteRepo.findByCorreo(correo);
       
        if (existe.isPresent()) {
            throw new RuntimeException("Este correo ya esta registrado");
        }
    }

    /**
     * Método comprueba la edad de la persona que crea la cuenta
     * @param fecha
     */
    public void validarEdad(LocalDate fechaNacimiento) throws Exception {

        LocalDate fechaActual = LocalDate.now();
        
        int edad = Period.between(fechaNacimiento, fechaActual).getYears();

        if (edad <= 18) {
            throw new Exception("El cliente debe ser mayor de edad para registrarse");
        }
    }

    /**
     * Método para validar el estado de un cliente
     * @param cliente
     */
    public void validarEstado(Cliente cliente) throws Exception {

        if (!cliente.getEstado()) {
            throw new Exception("El cliente no esta activo, debe activarla con el enlace que fue enviado a su correo");
        }
    }

    // SECTION: Implementacion de servicios

    @Override
    public Cliente login(@Valid String correo, String password) throws Exception {

        Cliente cliente = comprobarAutenticacion(correo, password);

        validarEstado(cliente);
    
        return cliente;
    }

    @Override
    public Cliente registrar(@Valid Cliente cliente) throws Exception {

        validarExisteCedula(cliente.getCedula());
        validarExisteCorreo(cliente.getCorreo());
        validarEdad(cliente.getFechaNacimiento());

        /* StrongPasswordEncryptor spe = new StrongPasswordEncryptor();
        cliente.setPassword(spe.encryptPassword(cliente.getPassword())); */
        Cliente registro = clienteRepo.save(cliente);

        /* AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword("teclado");

        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("America/Bogota"));

        String param1 = textEncryptor.encrypt(registro.getCorreo());
        String param2 = textEncryptor.encrypt(""+zdt.toInstant().toEpochMilli());

        emailServicio.enviarEmail("Registro en unicine", "Por favor acceda al siguiente enlace para activar la cuenta: http://localhost:8080/activar_cuenta.xhtml?p1="+param1+"&p2="+param2, cliente.getCorreo()); */
        return registro;
    }

    @Override
    public Cliente actualizar(@Valid Cliente cliente) throws Exception {

        obtener(cliente.getCedula());
        
        return clienteRepo.save(cliente);
    }

    @Override
    public void eliminar(@Valid Integer cedula) throws Exception {

        validarFormatoCedula(cedula);

        Optional<Cliente> eliminado = obtener(cedula);

        clienteRepo.delete(eliminado.get());
    }

    @Override
    public Optional<Cliente> obtener(@Valid Integer cedula) throws Exception {

        validarFormatoCedula(cedula);

        Optional<Cliente> buscado = clienteRepo.findById(cedula);

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<Cliente> listar() { return clienteRepo.findAll(); }

}
