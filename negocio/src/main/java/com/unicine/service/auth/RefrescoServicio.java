package com.unicine.service.auth;

import com.unicine.security.UsuarioPrincipal;
import com.unicine.transfer.dto.auth.ParTokensResponse;

/**
 * Puerto de sesiones: emite, rota y revoca refresh tokens en servidor.
 */
public interface RefrescoServicio {

    /**
     * Crea sesion: emite par access+refresh y persiste el hash del refresh.
     */
    ParTokensResponse crearSesion(UsuarioPrincipal principal);

    /**
     * Rota: revoca el refresh usado y emite un par nuevo.
     * Reusar un refresh revocado revoca TODAS las sesiones de la cedula.
     */
    ParTokensResponse rotar(String refreshToken);

    /**
     * Cierra sesion: revoca el refresh. Idempotente.
     */
    void cerrarSesion(String refreshToken);
}
