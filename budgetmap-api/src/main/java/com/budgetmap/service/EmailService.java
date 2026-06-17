package com.budgetmap.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EmailService {

    @Value("${budgetmap.email.api-key}")
    private String apiKey;

    @Value("${budgetmap.email.sender}")
    private String senderEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarCorreo(String to, String subject, String text) {
        try {
            String url = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);
            headers.set("accept", "application/json");

            Map<String, Object> body = new HashMap<>();
            
            Map<String, String> sender = new HashMap<>();
            sender.put("name", "BudgetMap API");
            sender.put("email", senderEmail);
            body.put("sender", sender);

            Map<String, String> toRecipient = new HashMap<>();
            toRecipient.put("email", to);
            body.put("to", List.of(toRecipient));

            body.put("subject", subject);
            body.put("htmlContent", text);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Correo web enviado con éxito a {} a través de Brevo API", to);
            } else {
                log.error("Fallo al enviar el correo web a {}. Código: {}", to, response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error crítico al enviar el correo por Brevo API a {}: {}", to, e.getMessage());
        }
    }
}
