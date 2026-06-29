package com.unicine.service.notification;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
     * Método para enviar un correo electrónico.
     * @param asunto El asunto del correo.
     * @param contenido El contenido del correo.
     * @param destinatario La dirección de correo del destinatario.
     * @return true si el correo se envió correctamente, false en caso contrario.
     */
    public boolean enviarEmail(String asunto, String contenido, String destinatario) {

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

            // Retornar true si el correo se envió correctamente
            return true;

        } catch (Exception e) {

            log.error("Fallo al enviar correo a {} con asunto '{}'", destinatario, asunto, e);
        }

        // Retornar false si hubo algún error al enviar el correo
        return false;
    }
}
