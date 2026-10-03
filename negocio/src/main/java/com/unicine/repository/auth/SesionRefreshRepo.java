package com.unicine.repository.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unicine.entity.auth.SesionRefresh;

@Repository
public interface SesionRefreshRepo extends JpaRepository<SesionRefresh, Long> {

    /**
     * Busca sesion por hash del refresh token.
     */
    Optional<SesionRefresh> findByTokenHash(String tokenHash);

    /**
     * Todas las sesiones de una cedula (para revocacion total ante reuso).
     */
    List<SesionRefresh> findByCedula(Integer cedula);
}
