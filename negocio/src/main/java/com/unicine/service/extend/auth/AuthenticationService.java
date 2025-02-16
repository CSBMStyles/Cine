package com.unicine.service.extend.auth;

import org.springframework.stereotype.Service;

import com.unicine.entity.Persona;
import com.unicine.service.AdministradorServicioImp;
import com.unicine.service.AdministradorTeatroServicioImp;
import com.unicine.service.ClienteServicioImp;
import com.unicine.service.PersonaServicio;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final List<PersonaServicio<? extends Persona>> servicios;

    public AuthenticationService(
            ClienteServicioImp clienteServicio, 
            AdministradorServicioImp adminServicio,
            AdministradorTeatroServicioImp adminTeatroServicio) {
        this.servicios = List.of(clienteServicio, adminServicio, adminTeatroServicio);
    }

    public Persona login(String correo, String password) throws Exception {
        return servicios.stream()
                .map(servicio -> {
                    try {
                        return Optional.of(servicio.login(correo, password));
                    } catch (Exception e) {
                        return Optional.<Persona>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new Exception("Credenciales inválidas"));
    }
}
