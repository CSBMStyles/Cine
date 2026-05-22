package com.unicine.service.user;

import java.util.List;
import java.util.Optional;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.user.Administrador;
import com.unicine.repository.user.AdministradorRepo;
import com.unicine.util.validation.group.OnCreate;
import com.unicine.util.validation.group.OnUpdate;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import com.unicine.util.validation.catalog.ErrorCatalog;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.exception.ValidationException;
import com.unicine.exception.AuthenticationException;

@Service
@Validated
public class AdministradorServicioImp implements PersonaServicio<Administrador> {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final AdministradorRepo administradorRepo;

    // Instanciamos el encriptador en este punto para no tener que instanciarlo cada vez que se usalo en los metodos que lo usan
    private final PasswordEncryptor encriptador = new StrongPasswordEncryptor();

    private final Validator validator;

    public AdministradorServicioImp(AdministradorRepo administradorRepo, Validator validator) {
        this.administradorRepo = administradorRepo;
        this.validator = validator;
    }

     // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la autenticacion de un administrador
     * @param numero
     * @throws
     */
    private Administrador comprobarAutenticacion(String correo, String password) throws Exception {

        Optional<Administrador> administrador = administradorRepo.findByCorreo(correo);

        if (administrador.isEmpty()) {
            throw new AuthenticationException(ErrorCatalog.AUTH002);
        }

        if (!encriptador.checkPassword(password, administrador.get().getPassword())) {
            throw new AuthenticationException(ErrorCatalog.AUTH003);
        }

        return administrador.get();
    }

    /**
     * Metodo para comprobar la presencia del administrador que se esta buscando
     * @param numero
     * @throws
     */
    private void validarExiste(Optional<Administrador> administrador) throws Exception {

        if (administrador.isEmpty()) {
            throw new ResourceNotFoundException(ErrorCatalog.ENT001);
        }
    }

    /**
     * Metodo para comprobar la presencia de una cedula en la base de datos
     * @param numero
     * @throws
     */
    private void validarExisteCedula(Integer numero) throws Exception {

        Optional<Administrador> existe = administradorRepo.findById(numero);
        
        if (existe.isPresent()) {
            throw new ValidationException(ErrorCatalog.DUP001);
        }
    }

    /**
     * Método que verifica si el correo ya esta registrado
     * @param correo
     * @return si existe el correo devuelve true, de lo contrario false
     */
    private void validarExisteCorreo(String correo) {

        Optional<Administrador> existe = administradorRepo.findByCorreo(correo);
       
        if (existe.isPresent()) {
            throw new RuntimeException(ErrorCatalog.DUP002.getMessage());
        }
    }

    /**
     * Método que verifica si existe un administrador con el mismo correo mediante la cedula, excluyendo el cliente que se esta actualizando, ya que se puede o no modificar la cedula
     * @param correo
     * @param cedula
     * @return si existe el correo devuelve true, de lo contrario false
     */
    private void validarRepiteCorreo(String correoModificar, Integer cedula) throws Exception {

        Optional<Administrador> existe = administradorRepo.buscarCorreoExcluido(correoModificar, cedula);
       
        if (existe.isPresent()) {
            throw new RuntimeException(ErrorCatalog.DUP002.getMessage());
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
      * Metodo para encriptar la contraseña de un administrador
     * @param administrador
     */
    private void encriptar(Administrador administrador) { administrador.setPassword(encriptador.encryptPassword(administrador.getPassword())); }

    /**
     * Valida el administrador usando el grupo OnCreate para aplicar las restricciones de password en texto plano.
     */
    private void validarParaRegistro(Administrador administrador) {

        var violaciones = validator.validate(administrador, OnCreate.class);

        if (!violaciones.isEmpty()) {
            throw new ConstraintViolationException(violaciones);
        }
    }

    // SECTION: Metodos del servicio

    // 1️⃣ Funciones del Administrador

    @Override
    public Administrador login(@Valid String correo, String password) throws Exception {

        Administrador administrador = comprobarAutenticacion(correo, password);

        return  administrador;
    }

    @Override
    public Administrador registrar(@Validated(OnCreate.class) Administrador administrador) throws Exception {

        validarParaRegistro(administrador);
        
        validarExisteCedula(administrador.getCedula());
        validarExisteCorreo(administrador.getCorreo());

        encriptar(administrador);

        Administrador registro = administradorRepo.save(administrador);

        /* AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword("teclado");

        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("America/Bogota"));

        String param1 = textEncryptor.encrypt(registro.getCorreo());
        String param2 = textEncryptor.encrypt(""+zdt.toInstant().toEpochMilli());

        emailServicio.enviarEmail("Registro en unicine", "Por favor acceda al siguiente enlace para activar la cuenta: http://localhost:8080/activar_cuenta.xhtml?p1="+param1+"&p2="+param2, administrador.getCorreo()); */
        return registro;
    }

    @Override
    public Administrador actualizar(@Validated(OnUpdate.class) Administrador administrador) throws Exception {

        validarRepiteCorreo(administrador.getCorreo(), administrador.getCedula());
        
        return administradorRepo.save(administrador);
    }

    @Override
    public Administrador cambiarPassword(@Validated(OnCreate.class) Administrador administrador, String passwordActual, String passwordNuevo) throws Exception {
        
    // 2. Verificar que el password actual es correcto
    if (!encriptador.checkPassword(passwordActual, administrador.getPassword())) {
        throw new AuthenticationException(ErrorCatalog.AUTH004);
    }

    // 3. Verificar que el nuevo password sea diferente al actual
    if (encriptador.checkPassword(passwordNuevo, administrador.getPassword())) {
        throw new AuthenticationException(ErrorCatalog.AUTH005);
    }

    administrador.setPassword(passwordNuevo);
    validarParaRegistro(administrador);
    encriptar(administrador);

    return administradorRepo.save(administrador);
}

    @Override
    public void eliminar(@Valid Administrador administrador, boolean confirmacion) throws Exception {

        comprobarConfirmacion(confirmacion);

        administradorRepo.delete(administrador);
    }

    @Override
    public Optional<Administrador> obtener(Integer cedula) throws Exception {

        Optional<Administrador> buscado = administradorRepo.findById(cedula);

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<Administrador> listar() {

        return administradorRepo.findAll();
    }

}
