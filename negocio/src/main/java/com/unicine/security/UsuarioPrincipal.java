package com.unicine.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    public UsuarioPrincipal(Integer cedula, String correo, String password, TipoUsuario tipo) {
        this.cedula = cedula;
        this.correo = correo;
        this.password = password;
        this.tipo = tipo;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + tipo.name()));
    }

    public static UsuarioPrincipal desdePersona(Persona persona, TipoUsuario tipo) {
        return new UsuarioPrincipal(persona.getCedula(), persona.getCorreo(), persona.getPassword(), tipo);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
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
