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
import com.unicine.transfer.dto.request.ClienteRequest;
import com.unicine.transfer.dto.response.ClienteResponse;
import com.unicine.transfer.mapper.ClienteMapper;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;
import com.unicine.util.validation.group.OnCreate;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.exception.ValidationException;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.AuthenticationException;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

/**
 * Servicio de registro, autenticacion y mantenimiento de clientes.
 */
@Service
@Validated
public class ClienteServicioImp implements ClienteServicio {

    private final ClienteRepo clienteRepo;

    private final ClienteMapper clienteMapper;

    private final PasswordEncryptor encriptador = new StrongPasswordEncryptor();

    private final Validator validator;

    public ClienteServicioImp(ClienteRepo clienteRepo, ClienteMapper clienteMapper, Validator validator) {
        this.clienteRepo = clienteRepo;
        this.clienteMapper = clienteMapper;
        this.validator = validator;
    }

    // SECTION: Metodos de soporte

    private Cliente comprobarAutenticacion(String correo, String password) throws Exception {
        Cliente cliente = obtenerClientePorCorreo(correo);
        validarPassword(password, cliente);
        return cliente;
    }

    private Cliente obtenerClientePorCorreo(String correo) {
        return clienteRepo.findByCorreo(correo)
                .orElseThrow(() -> new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_EMAIL_NOT_FOUND));
    }

    /**
     * Comprueba la credencial antes de permitir el acceso al cliente.
     */
    private void validarPassword(String password, Cliente cliente) {
        if (!encriptador.checkPassword(password, cliente.getPassword())) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_AUTH_DATA_INCORRECT);
        }
    }

    private void validarExiste(Optional<Cliente> cliente) throws Exception {

        if (cliente.isEmpty()) {
            throw new ResourceNotFoundException(UserErrorCatalog.DOMAIN_USER_ENTITY_CLIENT_NOT_FOUND);
        }
    }

    private void validarExisteCedula(Integer numero) throws Exception {

        Optional<Cliente> existe = clienteRepo.findById(numero);
        
        if (existe.isPresent()) {
            throw new ValidationException(UserErrorCatalog.DOMAIN_USER_DUPLICATE_ID_ALREADY_REGISTERED);
        }
    }

    private void validarExisteCorreo(String correo) {

        Optional<Cliente> existe = clienteRepo.findByCorreo(correo);
       
        if (existe.isPresent()) {
            throw new RuntimeException(UserErrorCatalog.DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED.getMessage());
        }
    }

    private void validarRepiteCorreo(String correoModificar, Integer cedula) throws Exception {

        Optional<Cliente> existe = clienteRepo.buscarCorreoExcluido(correoModificar, cedula);
       
        if (existe.isPresent()) {
            throw new RuntimeException(UserErrorCatalog.DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED.getMessage());
        }
    }

    public void validarEdad(LocalDate fechaNacimiento) throws Exception {

        LocalDate fechaActual = LocalDate.now();
        
        int edad = Period.between(fechaNacimiento, fechaActual).getYears();

        if (edad <= 18) {
            throw new BusinessRuleException(UserErrorCatalog.DOMAIN_USER_BUSINESS_RULE_CLIENT_UNDERAGE);
        }
    }

    public void validarEstado(Cliente cliente) throws Exception {

        if (!cliente.getEstado()) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_CLIENT_INACTIVE);
        }
    }

    private void comprobarConfirmacion(boolean confirmacion) {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

    private void encriptar(Cliente cliente) { cliente.setPassword(encriptador.encryptPassword(cliente.getPassword())); }

    private void validarParaRegistro(Cliente cliente) {

        var violaciones = validator.validate(cliente, OnCreate.class);

        if (!violaciones.isEmpty()) {
            throw new ConstraintViolationException(violaciones);
        }
    }

    // !SECTION
    // SECTION: Implementacion de servicios

    @Override
    public Cliente login(String correo, String password) throws Exception {

        Cliente cliente = comprobarAutenticacion(correo, password);

        validarEstado(cliente);
    
        return cliente;
    }

    @Override
    public ClienteResponse registrar(ClienteRequest request) throws Exception {

        Cliente cliente = clienteMapper.toEntity(request);

        validarParaRegistro(cliente);

        validarExisteCedula(cliente.getCedula());
        validarExisteCorreo(cliente.getCorreo());
        validarEdad(cliente.getFechaNacimiento());

        encriptar(cliente);

        Cliente registro = clienteRepo.save(cliente);

        return clienteMapper.toResponse(registro);
    }

    @Override
    public ClienteResponse actualizar(ClienteRequest request) throws Exception {

        validarRepiteCorreo(request.getCorreo(), request.getCedula());

        Cliente cliente = clienteMapper.toEntity(request);

        Cliente actualizado = clienteRepo.save(cliente);

        return clienteMapper.toResponse(actualizado);
    }

    @Override
    public Cliente cambiarPassword(Cliente cliente, String passwordActual, String passwordNuevo) throws Exception {
        validarPasswordActual(cliente, passwordActual);
        validarPasswordNueva(cliente, passwordNuevo);

        cliente.setPassword(passwordNuevo);
        validarParaRegistro(cliente);
        encriptar(cliente);

        return clienteRepo.save(cliente);
    }

    private void validarPasswordActual(Cliente cliente, String passwordActual) {
        if (!encriptador.checkPassword(passwordActual, cliente.getPassword())) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_CURRENT_PASSWORD_INCORRECT);
        }
    }

    private void validarPasswordNueva(Cliente cliente, String passwordNuevo) {
        if (encriptador.checkPassword(passwordNuevo, cliente.getPassword())) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_NEW_PASSWORD_SAME_AS_CURRENT);
        }
    }

    @Override
    public void eliminar(Integer cedula, boolean confirmacion) throws Exception {

        comprobarConfirmacion(confirmacion);

        Optional<Cliente> buscado = clienteRepo.findById(cedula);

        validarExiste(buscado);

        clienteRepo.delete(buscado.get());
    }

    @Override
    public Optional<ClienteResponse> obtener(Integer cedula) throws Exception {

        Optional<Cliente> buscado = clienteRepo.findById(cedula);

        validarExiste(buscado);

        return buscado.map(clienteMapper::toResponse);
    }

    @Override
    public List<ClienteResponse> listar() { return clienteMapper.toResponseList(clienteRepo.findAll()); }

    // !SECTION
}
