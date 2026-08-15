package com.unicine.util.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "image.identifier")
public class ImagenIdentificadorConfig {

    // SECTION: Atributos

    private String secret;

    // !SECTION
    // SECTION: Acceso

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    // !SECTION
}
