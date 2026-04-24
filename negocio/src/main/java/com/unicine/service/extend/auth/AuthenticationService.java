package com.unicine.service.extend.auth;

import org.springframework.stereotype.Service;

import com.unicine.entity.Persona;
import com.unicine.service.AdministradorServicioImp;
import com.unicine.service.AdministradorTeatroServicioImp;
import com.unicine.service.ClienteServicioImp;
import com.unicine.service.PersonaServicio;

import java.util.List;

@Service
public class AuthenticationService {

    // La autenticacion se intenta en cascada por tipo de usuario.
    // El orden de esta lista define el orden de evaluacion.
    private final List<PersonaServicio<? extends Persona>> servicios;

    public AuthenticationService(
            ClienteServicioImp clienteServicio, 
            AdministradorServicioImp adminServicio,
            AdministradorTeatroServicioImp adminTeatroServicio) {

        this.servicios = List.of(clienteServicio, adminServicio, adminTeatroServicio);
    }

    public Persona login(String correo, String password) throws Exception {

        // Estrategia: un solo formulario (correo + password), sin pedir rol.
        // Si un servicio autentica correctamente, se retorna esa Persona.
        for (PersonaServicio<? extends Persona> servicio : servicios) {
            try {
                return servicio.login(correo, password);

            } catch (Exception ignored) {
                // Se ignora para intentar con el siguiente perfil.
            }
        }

        throw new Exception("Credenciales inválidas");
    }
}
