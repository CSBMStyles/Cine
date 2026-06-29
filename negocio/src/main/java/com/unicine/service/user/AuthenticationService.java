package com.unicine.service.user;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.user.Administrador;
import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.entity.user.Cliente;
import com.unicine.entity.user.Persona;

import java.util.List;

import com.unicine.util.validation.catalog.domain.UserErrorCatalog;
import com.unicine.exception.AuthenticationException;

import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
public class AuthenticationService {

    // La autenticacion se intenta en cascada por tipo de usuario.
    // El orden de esta lista define el orden de evaluacion.
    private final List<PersonaServicio<? extends Persona>> servicios;

    public AuthenticationService(
            PersonaServicio<Cliente> clienteServicio,
            PersonaServicio<Administrador> adminServicio,
            PersonaServicio<AdministradorTeatro> adminTeatroServicio) {

        this.servicios = List.of(clienteServicio, adminServicio, adminTeatroServicio);
    }

    public Persona login(
            @NotBlank(message = "El correo no puede estar en blanco") String correo,
            @NotBlank(message = "La contrasena no puede estar en blanco") String password) {

        // Estrategia: un solo formulario (correo + password), sin pedir rol.
        // Si un servicio autentica correctamente, se retorna esa Persona.
        for (PersonaServicio<? extends Persona> servicio : servicios) {
            try {
                return servicio.login(correo, password);

            } catch (Exception e) {
                // Logueamos a nivel DEBUG para no contaminar los logs de login exitoso.
                // Si ningun servicio autentica, el AuthenticationException final
                // sera el unico registro visible a nivel INFO/WARN/ERROR.
                log.debug("Perfil {} rechazo las credenciales para '{}': {}",
                        servicio.getClass().getSimpleName(), correo, e.getMessage());
            }
        }

        throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_INVALID_CREDENTIALS);
    }
}
