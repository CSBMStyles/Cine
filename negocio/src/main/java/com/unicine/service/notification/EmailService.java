package com.unicine.service.notification;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.unicine.exception.ExternalServiceException;
import com.unicine.util.validation.catalog.domain.NotificationErrorCatalog;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

    private static final String REMITENTE = "no_reply@unicine.com";

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Metodo para enviar un correo electronico.
     * Lanza {@link ExternalServiceException} con el codigo
     * {@link NotificationErrorCatalog#DOMAIN_NOTIFICATION_EXTERNAL_SEND_ERROR}
     * si el servidor SMTP rechaza el envio.
     *
     * @param asunto El asunto del correo.
     * @param contenido El contenido del correo (admite HTML).
     * @param destinatario La direccion de correo del destinatario.
     */
    public void enviarEmail(String asunto, String contenido, String destinatario) {

        // Crear un mensaje
        MimeMessage mensaje = javaMailSender.createMimeMessage();

        // Ayudante para configurar el mensaje
        MimeMessageHelper helper = new MimeMessageHelper(mensaje);

        try {
            // Establecer el asunto del correo
            helper.setSubject(asunto);

            // Establecer el contenido del correo y permitir HTML
            helper.setText(contenido, true);

            // Establecer el destinatario del correo
            helper.setTo(destinatario);

            // Establecer el remitente del correo
            helper.setFrom(REMITENTE);

            // Enviar el mensaje
            javaMailSender.send(mensaje);

        } catch (Exception e) {

            log.error("Fallo al enviar correo a {} con asunto '{}'", destinatario, asunto, e);
            throw new ExternalServiceException(
                    NotificationErrorCatalog.DOMAIN_NOTIFICATION_EXTERNAL_SEND_ERROR, e, e.getMessage());
        }
    }
}
