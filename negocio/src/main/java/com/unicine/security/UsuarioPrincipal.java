package com.unicine.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.entity.user.Persona;

import lombok.Getter;

/**
 * Principal de seguridad para UniCine.
 * Subject = cedula.toString(), claim tipo = TipoUsuario.
 * Preparado para que JWT 5.1 lo serialice sin acoplar a AuthenticationService.
 */
@Getter
public class UsuarioPrincipal implements UserDetails {

    private final Integer cedula;

    private final String correo;

    private final TipoUsuario tipo;

    @JsonIgnore
    private final String password;

    private final List<Integer> teatroIds;

    private final Collection<? extends GrantedAuthority> authorities;

    public UsuarioPrincipal(Integer cedula, String correo, String password, TipoUsuario tipo) {
        this(cedula, correo, password, tipo, List.of());
    }

    public UsuarioPrincipal(Integer cedula, String correo, String password, TipoUsuario tipo,
            List<Integer> teatroIds) {
        this.cedula = cedula;
        this.correo = correo;
        this.password = password;
        this.tipo = tipo;
        this.teatroIds = teatroIds == null ? List.of() : List.copyOf(teatroIds);
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + tipo.name()));
    }

    public static UsuarioPrincipal desdePersona(Persona persona, TipoUsuario tipo) {
        return desdePersona(persona, tipo, List.of());
    }

    public static UsuarioPrincipal desdePersona(Persona persona, TipoUsuario tipo, List<Integer> teatroIds) {
        List<Integer> ids = teatroIds;
        if ((ids == null || ids.isEmpty()) && persona instanceof AdministradorTeatro adminTeatro
                && adminTeatro.getTeatros() != null) {
            ids = adminTeatro.getTeatros().stream()
                    .filter(t -> t != null && t.getCodigo() != null)
                    .map(t -> t.getCodigo())
                    .toList();
        }
        if (ids == null) {
            ids = List.of();
        }
        return new UsuarioPrincipal(persona.getCedula(), persona.getCorreo(), persona.getPassword(), tipo, ids);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        // Username para Spring Security = cedula como string (estable, unico)
        return String.valueOf(cedula);
    }

    public String getCorreo() {
        return correo;
    }

    /**
     * Indica si el principal tiene rol administrador general.
     * Punto unico para el chequeo de rol — evita repetir el stream en controllers.
     */
    public boolean esAdministrador() {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }

    /**
     * Indica si el principal puede gestionar catalogos comerciales
     * (confiteria, presentaciones): ADMIN o ADMINISTRADOR_TEATRO.
     */
    public boolean esGestorCatalogo() {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR")
                        || a.getAuthority().equals("ROLE_ADMINISTRADOR_TEATRO"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
