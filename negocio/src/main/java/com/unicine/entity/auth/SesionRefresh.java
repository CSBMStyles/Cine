package com.unicine.entity.auth;

import java.io.Serializable;
import java.time.Instant;

import com.unicine.enums.user.TipoUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Sesion de refresco persistida para revocacion en servidor (5.1.3).
 * Guarda el hash SHA-256 del token, nunca el token en claro.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class SesionRefresh implements Serializable {

    // SECTION: Atributos

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private Integer cedula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsuario tipo;

    @Column(nullable = false)
    private Instant expiraEn;

    @Column(nullable = false)
    private Boolean revocado;

    @Column(nullable = false, updatable = false)
    private Instant creadoEn;

    // !SECTION
}
