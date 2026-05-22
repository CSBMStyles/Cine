package com.unicine.service.user;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.user.Cliente;
import com.unicine.repository.user.ClienteRepo;
import com.unicine.util.validation.group.OnCreate;
import com.unicine.util.validation.group.OnUpdate;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import com.unicine.util.validation.catalog.ErrorCatalog;

@Service
@Validated
public class ClienteServicioImp implements PersonaServicio<Cliente> {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final ClienteRepo clienteRepo;

    // Instanciamos el encriptador en este punto para no tener que instanciarlo cada vez que se usalo en los metodos que lo usan
    private final PasswordEncryptor encriptador = new StrongPasswordEncryptor();

    private final Validator validator;

    public ClienteServicioImp(ClienteRepo clienteRepo, Validator validator) {
        this.clienteRepo = clienteRepo;
        this.validator = validator;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la autenticacion de un cliente
     * @param numero
     * @throws
     */
    private Cliente comprobarAutenticacion(String correo, String password) throws Exception {

        Optional<Cliente> cliente = clienteRepo.findByCorreo(correo);

        if (cliente.isEmpty()) {
            throw new Exception(ErrorCatalog.AUTH002.getMessage());
        }

        if (!encriptador.checkPassword(password, cliente.get().getPassword())) {
            throw new Exception(ErrorCatalog.AUTH003.getMessage());
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
            throw new Exception(ErrorCatalog.ENT003.getMessage());
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
            throw new Exception(ErrorCatalog.DUP001.getMessage());
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
            throw new RuntimeException(ErrorCatalog.DUP002.getMessage());
        }
    }

    /**
     * Método que verifica si existe un cliente con el mismo correo mediante la cedula, excluyendo el cliente que se esta actualizando, ya que se puede o no modificar la cedula
     * @param cliente
     * @return si existe el correo devuelve true, de lo contrario false
     */
    private void validarRepiteCorreo(String correoModificar, Integer cedula) throws Exception {

        Optional<Cliente> existe = clienteRepo.buscarCorreoExcluido(correoModificar, cedula);
       
        if (existe.isPresent()) {
            throw new RuntimeException(ErrorCatalog.DUP002.getMessage());
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
            throw new Exception(ErrorCatalog.REG001.getMessage());
        }
    }

    /**
     * Método para validar el estado de un cliente
     * @param cliente
     */
    public void validarEstado(Cliente cliente) throws Exception {

        if (!cliente.getEstado()) {
            throw new Exception(ErrorCatalog.AUTH006.getMessage());
        }
    }

    /**
     * Metodo para comprobar la presencia de las relaciones del teatro
     * @param teatro
     * @throws
     */
    private void comprobarConfirmacion(boolean confirmacion) {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

    /**
      * Metodo para encriptar la contraseña de un cliente
     * @param cliente
     */
    private void encriptar(Cliente cliente) { cliente.setPassword(encriptador.encryptPassword(cliente.getPassword())); }

    /**
     * Valida usando OnCreate para asegurar reglas de password en texto plano.
     */
    private void validarParaRegistro(Cliente cliente) {

        var violaciones = validator.validate(cliente, OnCreate.class);

        if (!violaciones.isEmpty()) {
            throw new ConstraintViolationException(violaciones);
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
    public Cliente registrar(@Validated(OnCreate.class) Cliente cliente) throws Exception {

        validarParaRegistro(cliente);

        validarExisteCedula(cliente.getCedula());
        validarExisteCorreo(cliente.getCorreo());
        validarEdad(cliente.getFechaNacimiento());

        encriptar(cliente);

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
    public Cliente actualizar(@Validated(OnUpdate.class) Cliente cliente) throws Exception {

        validarRepiteCorreo(cliente.getCorreo(), cliente.getCedula());

        return clienteRepo.save(cliente);
    }

    @Override
    public Cliente cambiarPassword(@Validated(OnCreate.class) Cliente cliente, String passwordActual, String passwordNuevo) throws Exception {

        if (!encriptador.checkPassword(passwordActual, cliente.getPassword())) {
            throw new Exception(ErrorCatalog.AUTH004.getMessage());
        }

        if (encriptador.checkPassword(passwordNuevo, cliente.getPassword())) {
            throw new Exception(ErrorCatalog.AUTH005.getMessage());
        }

        cliente.setPassword(passwordNuevo);
        validarParaRegistro(cliente);
        encriptar(cliente);

        return clienteRepo.save(cliente);
    }

    @Override
    public void eliminar(@Valid Cliente cliente, boolean confirmacion) throws Exception {

        comprobarConfirmacion(confirmacion);

        clienteRepo.delete(cliente);
    }

    @Override
    public Optional<Cliente> obtener(Integer cedula) throws Exception {

        Optional<Cliente> buscado = clienteRepo.findById(cedula);

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<Cliente> listar() { return clienteRepo.findAll(); }

}
