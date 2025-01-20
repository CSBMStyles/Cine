package com.unicine.servicio.complementos.auth;

import org.springframework.stereotype.Service;
import com.unicine.entidades.Persona;
import com.unicine.servicio.AdministradorServicioImp;
import com.unicine.servicio.AdministradorTeatroServicioImp;
import com.unicine.servicio.ClienteServicioImp;
import com.unicine.servicio.PersonaServicio;

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
