package com.unicine.service.confiteria;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.confiteria.ConfiteriaPresentacion;
import com.unicine.repository.confiteria.ConfiteriaPresentacionRepo;

/**
 * Job para revertir descuentos temporales de confiteria que hayan alcanzado
 * su fecha de expiracion.
 * 
 * Se ejecuta diariamente a medianoche. Las presentaciones con precio temporal
 * y fecha de expiracion vencida vuelven a su precio base.
 */
@Service
public class ConfiteriaPresentacionJob {

    private final ConfiteriaPresentacionRepo presentacionRepo;

    public ConfiteriaPresentacionJob(ConfiteriaPresentacionRepo presentacionRepo) {
        this.presentacionRepo = presentacionRepo;
    }

    /**
     * Revierte los descuentos temporales vencidos a su precio base.
     * 
     * El precio no cambia de valor numerico, solo se restaura el precio base
     * como precio de venta, por lo que no se genera historial.
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // A medianoche
    public void revertirDescuentosVencidos() {
        LocalDateTime ahora = LocalDateTime.now(ZoneId.of("America/Bogota"));

        List<ConfiteriaPresentacion> vencidas = presentacionRepo.findPresentacionesConDescuentoVencido(ahora);

        for (ConfiteriaPresentacion presentacion : vencidas) {
            presentacion.setPrecio(presentacion.getPrecioBase());
            presentacion.setFechaExpiracionTemporal(null);
            presentacionRepo.save(presentacion);
        }
    }
}
