package com.unicine.repository.payment;

import com.unicine.entity.payment.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PagoRepo extends JpaRepository<Pago, Long> {

    // SECTION: Consultas por claves de idempotencia

    Optional<Pago> findByCompraCodigo(Integer compraCodigo);

    Optional<Pago> findByMercadoPagoId(String mercadoPagoId);

    Optional<Pago> findByIdempotencyKey(String idempotencyKey);

    boolean existsByCompraCodigo(Integer compraCodigo);

    // !SECTION
}
