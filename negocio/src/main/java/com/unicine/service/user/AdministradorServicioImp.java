package com.unicine.service.user;

import java.util.List;
import java.util.Optional;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.user.Administrador;
import com.unicine.repository.user.AdministradorRepo;
import com.unicine.transfer.dto.request.AdministradorRequest;
import com.unicine.transfer.dto.response.AdministradorResponse;
import com.unicine.transfer.mapper.AdministradorMapper;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;
import com.unicine.util.validation.group.OnCreate;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.exception.ValidationException;
import com.unicine.exception.AuthenticationException;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Service
@Validated
public class AdministradorServicioImp implements AdministradorServicio {

    private final AdministradorRepo administradorRepo;

    private final AdministradorMapper administradorMapper;

    private final PasswordEncryptor encriptador = new StrongPasswordEncryptor();

    private final Validator validator;

    public AdministradorServicioImp(AdministradorRepo administradorRepo, AdministradorMapper administradorMapper, Validator validator) {
        this.administradorRepo = administradorRepo;
        this.administradorMapper = administradorMapper;
        this.validator = validator;
    }

     // SECTION: Metodos de soporte

    private Administrador comprobarAutenticacion(String correo, String password) throws Exception {

        Optional<Administrador> administrador = administradorRepo.findByCorreo(correo);

        if (administrador.isEmpty()) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_EMAIL_NOT_FOUND);
        }

        if (!encriptador.checkPassword(password, administrador.get().getPassword())) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_AUTH_DATA_INCORRECT);
        }

        return administrador.get();
    }

    private void validarExiste(Optional<Administrador> administrador) throws Exception {

        if (administrador.isEmpty()) {
            throw new ResourceNotFoundException(UserErrorCatalog.DOMAIN_USER_ENTITY_ADMIN_NOT_FOUND);
        }
    }

    private void validarExisteCedula(Integer numero) throws Exception {

        Optional<Administrador> existe = administradorRepo.findById(numero);
        
        if (existe.isPresent()) {
            throw new ValidationException(UserErrorCatalog.DOMAIN_USER_DUPLICATE_ID_ALREADY_REGISTERED);
        }
    }

    private void validarExisteCorreo(String correo) {

        Optional<Administrador> existe = administradorRepo.findByCorreo(correo);
       
        if (existe.isPresent()) {
            throw new RuntimeException(UserErrorCatalog.DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED.getMessage());
        }
    }

    private void validarRepiteCorreo(String correoModificar, Integer cedula) throws Exception {

        Optional<Administrador> existe = administradorRepo.buscarCorreoExcluido(correoModificar, cedula);
       
        if (existe.isPresent()) {
            throw new RuntimeException(UserErrorCatalog.DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED.getMessage());
        }
    }

    private void comprobarConfirmacion(boolean confirmacion) {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

    private void encriptar(Administrador administrador) { administrador.setPassword(encriptador.encryptPassword(administrador.getPassword())); }

    private void validarParaRegistro(Administrador administrador) {

        var violaciones = validator.validate(administrador, OnCreate.class);

        if (!violaciones.isEmpty()) {
            throw new ConstraintViolationException(violaciones);
        }
    }

    // SECTION: Metodos del servicio

    @Override
    public Administrador login(String correo, String password) throws Exception {

        Administrador administrador = comprobarAutenticacion(correo, password);

        return  administrador;
    }

    @Override
    public AdministradorResponse registrar(AdministradorRequest request) throws Exception {

        Administrador administrador = administradorMapper.toEntity(request);

        validarParaRegistro(administrador);
        
        validarExisteCedula(administrador.getCedula());
        validarExisteCorreo(administrador.getCorreo());

        encriptar(administrador);

        Administrador registro = administradorRepo.save(administrador);

        return administradorMapper.toResponse(registro);
    }

    @Override
    public AdministradorResponse actualizar(AdministradorRequest request) throws Exception {

        validarRepiteCorreo(request.getCorreo(), request.getCedula());

        Administrador administrador = administradorMapper.toEntity(request);
        
        Administrador actualizado = administradorRepo.save(administrador);

        return administradorMapper.toResponse(actualizado);
    }

    @Override
    public Administrador cambiarPassword(Administrador administrador, String passwordActual, String passwordNuevo) throws Exception {
        
        if (!encriptador.checkPassword(passwordActual, administrador.getPassword())) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_CURRENT_PASSWORD_INCORRECT);
        }

        if (encriptador.checkPassword(passwordNuevo, administrador.getPassword())) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_NEW_PASSWORD_SAME_AS_CURRENT);
        }

        administrador.setPassword(passwordNuevo);
        validarParaRegistro(administrador);
        encriptar(administrador);

        return administradorRepo.save(administrador);
    }

    @Override
    public void eliminar(Integer cedula, boolean confirmacion) throws Exception {

        comprobarConfirmacion(confirmacion);

        Optional<Administrador> buscado = administradorRepo.findById(cedula);

        validarExiste(buscado);

        administradorRepo.delete(buscado.get());
    }

    @Override
    public Optional<AdministradorResponse> obtener(Integer cedula) throws Exception {

        Optional<Administrador> buscado = administradorRepo.findById(cedula);

        validarExiste(buscado);

        return buscado.map(administradorMapper::toResponse);
    }

    @Override
    public List<AdministradorResponse> listar() {

        return administradorMapper.toResponseList(administradorRepo.findAll());
    }

}
