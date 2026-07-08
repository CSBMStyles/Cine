package com.unicine.service.user;

import java.util.List;
import java.util.Optional;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.repository.user.AdministradorTeatroRepo;
import com.unicine.transfer.dto.request.AdministradorTeatroRequest;
import com.unicine.transfer.dto.response.AdministradorTeatroResponse;
import com.unicine.transfer.mapper.AdministradorTeatroMapper;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;
import com.unicine.util.validation.group.OnCreate;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.exception.ValidationException;
import com.unicine.exception.AuthenticationException;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Service
@Validated
public class AdministradorTeatroServicioImp implements AdministradorTeatroServicio {

    private final AdministradorTeatroRepo administradorTeatroRepo;

    private final AdministradorTeatroMapper administradorTeatroMapper;

    private final PasswordEncryptor encriptador = new StrongPasswordEncryptor();

    private final Validator validator;

    public AdministradorTeatroServicioImp(AdministradorTeatroRepo administradorTeatroRepo, AdministradorTeatroMapper administradorTeatroMapper, Validator validator) {
        this.administradorTeatroRepo = administradorTeatroRepo;
        this.administradorTeatroMapper = administradorTeatroMapper;
        this.validator = validator;
    }

    // SECTION: Metodos de soporte

    private AdministradorTeatro comprobarAutenticacion(String correo, String password) throws Exception {

        Optional<AdministradorTeatro> administrador = administradorTeatroRepo.findByCorreo(correo);

        if (administrador.isEmpty()) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_EMAIL_NOT_FOUND);
        }

        if (!encriptador.checkPassword(password, administrador.get().getPassword())) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_AUTH_DATA_INCORRECT);
        }

        return administrador.get();
    }

    private void validarExiste(Optional<AdministradorTeatro> administrador) throws Exception {

        if (administrador.isEmpty()) {
            throw new ResourceNotFoundException(UserErrorCatalog.DOMAIN_USER_ENTITY_ADMIN_NOT_FOUND);
        }
    }

    private void validarExisteCedula(Integer numero) throws Exception {

        Optional<AdministradorTeatro> existe = administradorTeatroRepo.findById(numero);
        
        if (existe.isPresent()) {
            throw new ValidationException(UserErrorCatalog.DOMAIN_USER_DUPLICATE_ID_ALREADY_REGISTERED);
        }
    }

    private void validarExisteCorreo(String correo) {

        Optional<AdministradorTeatro> existe = administradorTeatroRepo.findByCorreo(correo);
       
        if (existe.isPresent()) {
            throw new RuntimeException(UserErrorCatalog.DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED.getMessage());
        }
    }

    private void validarRepiteCorreo(String correoModificar, Integer cedula) throws Exception {

        Optional<AdministradorTeatro> existe = administradorTeatroRepo.buscarCorreoExcluido(correoModificar, cedula);
       
        if (existe.isPresent()) {
            throw new RuntimeException(UserErrorCatalog.DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED.getMessage());
        }
    }

    private void comprobarConfirmacion(boolean confirmacion) {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

    private void encriptar(AdministradorTeatro administrador) { administrador.setPassword(encriptador.encryptPassword(administrador.getPassword())); }

    private void validarParaRegistro(AdministradorTeatro administrador) {

        var violaciones = validator.validate(administrador, OnCreate.class);

        if (!violaciones.isEmpty()) {
            throw new ConstraintViolationException(violaciones);
        }
    }

    // SECTION: Metodos del servicio

    @Override
    public AdministradorTeatro login(String correo, String password) throws Exception {

        AdministradorTeatro administrador = comprobarAutenticacion(correo, password);

        return  administrador;
    }

    @Override
    public AdministradorTeatroResponse registrar(AdministradorTeatroRequest request) throws Exception {

        AdministradorTeatro administrador = administradorTeatroMapper.toEntity(request);

        validarParaRegistro(administrador);
        
        validarExisteCedula(administrador.getCedula());
        validarExisteCorreo(administrador.getCorreo());

        encriptar(administrador);
        
        AdministradorTeatro registro = administradorTeatroRepo.save(administrador);

        return administradorTeatroMapper.toResponse(registro);
    }

    @Override
    public AdministradorTeatroResponse actualizar(AdministradorTeatroRequest request) throws Exception {

        validarRepiteCorreo(request.getCorreo(), request.getCedula());

        AdministradorTeatro administrador = administradorTeatroMapper.toEntity(request);

        AdministradorTeatro actualizado = administradorTeatroRepo.save(administrador);

        return administradorTeatroMapper.toResponse(actualizado);
    }

    @Override
    public AdministradorTeatro cambiarPassword(AdministradorTeatro administrador, String passwordActual, String passwordNuevo) throws Exception {

        if (!encriptador.checkPassword(passwordActual, administrador.getPassword())) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_CURRENT_PASSWORD_INCORRECT);
        }

        if (encriptador.checkPassword(passwordNuevo, administrador.getPassword())) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_NEW_PASSWORD_SAME_AS_CURRENT);
        }

        administrador.setPassword(passwordNuevo);
        validarParaRegistro(administrador);
        encriptar(administrador);

        return administradorTeatroRepo.save(administrador);
    }

    @Override
    public void eliminar(Integer cedula, boolean confirmacion) throws Exception {

        comprobarConfirmacion(confirmacion);

        Optional<AdministradorTeatro> buscado = administradorTeatroRepo.findById(cedula);

        validarExiste(buscado);

        administradorTeatroRepo.delete(buscado.get());
    }

    @Override
    public Optional<AdministradorTeatroResponse> obtener(Integer cedula) throws Exception {

        Optional<AdministradorTeatro> buscado = administradorTeatroRepo.findById(cedula);
        
        validarExiste(buscado);

        return buscado.map(administradorTeatroMapper::toResponse);
    }

    @Override
    public List<AdministradorTeatroResponse> listar() { return administradorTeatroMapper.toResponseList(administradorTeatroRepo.findAll()); }

}
