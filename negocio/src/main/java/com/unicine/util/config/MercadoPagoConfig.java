package com.unicine.util.config;

import com.mercadopago.client.order.OrderClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mercadopago")
public class MercadoPagoConfig {

    // SECTION: Atributos

    private String accessToken;

    private String publicKey;

    private String webhookSecret;

    // !SECTION
    // SECTION: Acceso

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    // !SECTION
    // SECTION: Cliente

    // Bean separado para poder mockearlo en tests sin tocar la red.
    @Bean
    public OrderClient orderClient() {
        return new OrderClient();
    }

    // !SECTION
}
