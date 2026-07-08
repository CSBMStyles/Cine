package com.unicine.service.user;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.user.Persona;

import com.unicine.util.validation.catalog.domain.UserErrorCatalog;
import com.unicine.exception.AuthenticationException;

import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
public class AuthenticationService {

    private final ClienteServicio clienteServicio;
    private final AdministradorServicio administradorServicio;
    private final AdministradorTeatroServicio administradorTeatroServicio;

    public AuthenticationService(
            ClienteServicio clienteServicio,
            AdministradorServicio administradorServicio,
            AdministradorTeatroServicio administradorTeatroServicio) {

        this.clienteServicio = clienteServicio;
        this.administradorServicio = administradorServicio;
        this.administradorTeatroServicio = administradorTeatroServicio;
    }

    public Persona login(
            @NotBlank(message = "El correo no puede estar en blanco") String correo,
            @NotBlank(message = "La contrasena no puede estar en blanco") String password) {

        // Estrategia: un solo formulario (correo + password), sin pedir rol.
        // Si un servicio autentica correctamente, se retorna esa Persona.
        try {
            return clienteServicio.login(correo, password);

        } catch (Exception e) {
            log.debug("Perfil Cliente rechazo las credenciales para '{}': {}", correo, e.getMessage());
        }

        try {
            return administradorServicio.login(correo, password);

        } catch (Exception e) {
            log.debug("Perfil Administrador rechazo las credenciales para '{}': {}", correo, e.getMessage());
        }

        try {
            return administradorTeatroServicio.login(correo, password);

        } catch (Exception e) {
            log.debug("Perfil AdministradorTeatro rechazo las credenciales para '{}': {}", correo, e.getMessage());
        }

        throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_INVALID_CREDENTIALS);
    }
}
