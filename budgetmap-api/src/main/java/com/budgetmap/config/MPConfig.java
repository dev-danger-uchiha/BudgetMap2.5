package com.budgetmap.config;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MPConfig {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        if (accessToken == null || accessToken.isBlank()) {
            log.error("¡CRÍTICO! Access Token de Mercado Pago no configurado.");
            throw new IllegalStateException("Mercado Pago Access Token is missing");
        }
        MercadoPagoConfig.setAccessToken(accessToken);
        log.info("Mercado Pago SDK inicializado correctamente (Modo: {})", accessToken.startsWith("TEST") ? "SANDBOX" : "PRODUCCIÓN");
    }
}