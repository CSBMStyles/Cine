package com.unicine.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.unicine.entity.user.Administrador;
import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.entity.user.Cliente;
import com.unicine.entity.user.Persona;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.repository.user.AdministradorRepo;
import com.unicine.repository.user.AdministradorTeatroRepo;
import com.unicine.repository.user.ClienteRepo;
import com.unicine.service.user.AuthenticationService;

import lombok.extern.slf4j.Slf4j;

/**
 * UserDetailsService que delega en AuthenticationService pero adaptado a contrato Spring Security.
 * Este servicio es consumido por JWT en 5.1; 4.3 lo deja listo sin ciclo.
 * No genera JWT, solo resuelve Principal por correo.
 */
@Slf4j
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final AuthenticationService authenticationService;
    private final ClienteRepo clienteRepo;
    private final AdministradorRepo administradorRepo;
    private final AdministradorTeatroRepo administradorTeatroRepo;

    public UsuarioDetailsService(AuthenticationService authenticationService,
                                 ClienteRepo clienteRepo,
                                 AdministradorRepo administradorRepo,
                                 AdministradorTeatroRepo administradorTeatroRepo) {
        this.authenticationService = authenticationService;
        this.clienteRepo = clienteRepo;
        this.administradorRepo = administradorRepo;
        this.administradorTeatroRepo = administradorTeatroRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username es correo en nuestro flujo
        Optional<Cliente> cliente = clienteRepo.findByCorreo(username);
        if (cliente.isPresent()) {
            return UsuarioPrincipal.desdePersona(cliente.get(), TipoUsuario.CLIENTE);
        }
        Optional<Administrador> admin = administradorRepo.findByCorreo(username);
        if (admin.isPresent()) {
            return UsuarioPrincipal.desdePersona(admin.get(), TipoUsuario.ADMINISTRADOR);
        }
        Optional<AdministradorTeatro> adminTeatro = administradorTeatroRepo.findByCorreo(username);
        if (adminTeatro.isPresent()) {
            return UsuarioPrincipal.desdePersona(adminTeatro.get(), TipoUsuario.ADMINISTRADOR_TEATRO);
        }
        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }

    /**
     * Carga principal por correo usando AuthenticationService con validacion delegada.
     * Solo para uso interno post-login — no exponer password.
     */
    public UsuarioPrincipal loadByCorreo(String correo, String password) {
        Persona persona = authenticationService.login(correo, password);

        TipoUsuario tipo;
        String simpleName = persona.getClass().getSimpleName();
        switch (simpleName) {
            case "Cliente":
                tipo = TipoUsuario.CLIENTE;
                break;
            case "Administrador":
                tipo = TipoUsuario.ADMINISTRADOR;
                break;
            case "AdministradorTeatro":
                tipo = TipoUsuario.ADMINISTRADOR_TEATRO;
                break;
            default:
                tipo = TipoUsuario.CLIENTE;
                break;
        }

        log.debug("Principal resuelto correo={} tipo={} cedula={}", correo, tipo, persona.getCedula());
        return UsuarioPrincipal.desdePersona(persona, tipo);
    }

    /**
     * Carga por cedula para perfil /me sin password (desde SecurityContext).
     * Busca en los 3 repositorios via AuthenticationService no disponible,
     * por lo que delega en Cliente/Administrador servicios directamente si se requiere.
     */
    public UsuarioPrincipal loadByCedula(Integer cedula, TipoUsuario tipo, Persona persona) {
        return UsuarioPrincipal.desdePersona(persona, tipo);
    }
}
