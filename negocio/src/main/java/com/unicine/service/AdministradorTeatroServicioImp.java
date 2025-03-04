package com.unicine.service;

import java.util.List;
import java.util.Optional;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.AdministradorTeatro;
import com.unicine.repository.AdministradorTeatroRepo;
import com.unicine.util.validation.attributes.PersonaAtributoValidator;

import jakarta.validation.Valid;

@Service
@Validated
public class AdministradorTeatroServicioImp implements PersonaServicio<AdministradorTeatro> {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final AdministradorTeatroRepo administradorTeatroRepo;

    // Instanciamos el encriptador en este punto para no tener que instanciarlo cada vez que se usalo en los metodos que lo usan
    private final PasswordEncryptor encriptador = new StrongPasswordEncryptor();

    public AdministradorTeatroServicioImp(AdministradorTeatroRepo administradorTeatroRepo) {
        this.administradorTeatroRepo = administradorTeatroRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la autenticacion de un administrador
     * @param numero
     * @throws
     */
    private AdministradorTeatro comprobarAutenticacion(String correo, String password) throws Exception {

        Optional<AdministradorTeatro> administrador = administradorTeatroRepo.findByCorreo(correo);

        if (administrador.isEmpty()) {
            throw new Exception("El correo no existe");
        }

        if (!encriptador.checkPassword(password, administrador.get().getPassword())) {
            throw new Exception("Los datos de autenticación son incorrectos");
        }

        return administrador.get();
    }

    /**
     * Metodo para comprobar la presencia del administrador que se esta buscando
     * @param numero
     * @throws
     */
    private void validarExiste(Optional<AdministradorTeatro> administrador) throws Exception {

        if (administrador.isEmpty()) {
            throw new Exception("El administrador no existe");
        }
    }

    /**
     * Metodo para comprobar la presencia de una cedula en la base de datos
     * @param numero
     * @throws
     */
    private void validarExisteCedula(Integer numero) throws Exception {

        Optional<AdministradorTeatro> existe = administradorTeatroRepo.findById(numero);
        
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

        Optional<AdministradorTeatro> existe = administradorTeatroRepo.findByCorreo(correo);
       
        if (existe.isPresent()) {
            throw new RuntimeException("Este correo ya esta registrado");
        }
    }


    /**
     * Método que verifica si existe un administrador con el mismo correo mediante la cedula, excluyendo el cliente que se esta actualizando, ya que se puede o no modificar la cedula
     * @param correo
     * @param cedula
     * @return si existe el correo devuelve true, de lo contrario false
     */
    private void validarRepiteCorreo(String correoModificar, Integer cedula) throws Exception {

        Optional<AdministradorTeatro> existe = administradorTeatroRepo.buscarCorreoExcluido(correoModificar, cedula);
       
        if (existe.isPresent()) {
            throw new RuntimeException("Este correo ya esta registrado");
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
    * Metodo para transformar un String a un Integer
    * @param cedula
    * @return
    */
    private Integer transformar(String cedula) { return Integer.parseInt(cedula); }

    /**
     * Metodo para encriptar la contraseña de un administrador
     * @param administrador
     */
    private void encriptar(AdministradorTeatro administrador) { administrador.setPassword(encriptador.encryptPassword(administrador.getPassword())); }

    // SECTION: Metodos del servicio

    // 2️⃣ Funion del Administrador Teatro

    @Override
    public AdministradorTeatro login(@Valid String correo, String password) throws Exception {

        AdministradorTeatro administrador = comprobarAutenticacion(correo, password);

        return  administrador;
    }

    // 1️⃣ Funciones del Administrador

    @Override
    public AdministradorTeatro registrar(@Valid AdministradorTeatro administrador) throws Exception {
        
        validarExisteCedula(administrador.getCedula());
        validarExisteCorreo(administrador.getCorreo());

        encriptar(administrador);
        
        AdministradorTeatro registro = administradorTeatroRepo.save(administrador);

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
    public AdministradorTeatro actualizar(@Valid AdministradorTeatro administrador) throws Exception {

        validarRepiteCorreo(administrador.getCorreo(), administrador.getCedula());

        return administradorTeatroRepo.save(administrador);
    }

    @Override
    public void eliminar(@Valid AdministradorTeatro administrador, boolean confirmacion) throws Exception {

        comprobarConfirmacion(confirmacion);

        administradorTeatroRepo.delete(administrador);
    }

    @Override
    public Optional<AdministradorTeatro> obtener(@Valid PersonaAtributoValidator cedula) throws Exception {

        Optional <AdministradorTeatro> buscado = administradorTeatroRepo.findById(transformar(cedula.getCedula()));
        
        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<AdministradorTeatro> listar() { return administradorTeatroRepo.findAll(); }

}
