package com.budgetmap.service;

import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Usuario;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PasarelaService {

    @Value("${budgetmap.url.webhook}")
    private String webhookUrl;

    /**
     * Genera una preferencia de pago en Mercado Pago.
     * Principio: Defensive programming validando inputs antes de llamar a la API externa.
     */
    public String crearPreferenciaPago(Usuario usuario, String nombrePlan, BigDecimal precio) {
        if (usuario == null || nombrePlan == null || precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Datos de facturación inválidos para generar el pago.");
        }

        try {
            PreferenceClient client = new PreferenceClient();

            // 1. Configurar el ítem a cobrar
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title(nombrePlan)
                    .quantity(1)
                    .currencyId("COP")
                    .unitPrice(precio)
                    .build();
            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(item);

            // 2. Configurar el pagador (Payer) obligatorio para pasar las políticas de riesgo
            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .email(usuario.getEmail() != null ? usuario.getEmail() : "comprador@budgetmap.com")
                    .name(usuario.getNombre() != null ? usuario.getNombre() : "Usuario PRO")
                    .build();

            // 3. Configurar urls de retorno
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("https://budgetmap.com/pago-exitoso")
                    .failure("https://budgetmap.com/pago-fallido")
                    .pending("https://budgetmap.com/pago-pendiente")
                    .build();

            // 4. Construir la solicitud asociándola al usuario de nuestro sistema (External Reference)
            PreferenceRequest.PreferenceRequestBuilder requestBuilder = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .externalReference(usuario.getId().toString()); // Clave para conciliar en el Webhook

            if (webhookUrl != null && !webhookUrl.contains("tu-dominio-ngrok.com")) {
                requestBuilder.notificationUrl(webhookUrl);
            }
            PreferenceRequest request = requestBuilder.build();

            Preference preference = client.create(request);
            log.info("Preferencia creada exitosamente para usuario ID {}: {}", usuario.getId(), preference.getId());
            
            // En Sandbox se usa el sandbox_init_point, en PROD se usa init_point
            return preference.getSandboxInitPoint(); 

        } catch (MPApiException apiException) {
            String causaDetallada = apiException.getApiResponse().getContent();
            log.error("Error de la API de Mercado Pago. Status: {}, Causa: {}",
                    apiException.getApiResponse().getStatusCode(),
                    causaDetallada);
            throw new RuntimeException("Mercado Pago rechazó la petición: " + causaDetallada);
        } catch (MPException ex) {
            log.error("Error interno del SDK de Mercado Pago: {}", ex.getMessage());
            throw new RuntimeException("Error interno al procesar la pasarela.");
        }
    }

    /**
     * Procesa la notificación (Webhook) de Mercado Pago.
     * Aquí aplicamos @Transactional para asegurar consistencia en BD.
     */
    @Transactional
    public void procesarWebhook(String tipo, String dataId) {
        if ("payment".equals(tipo)) {
            log.info("Procesando webhook de pago con ID: {}", dataId);
            // TODO: Inicializar PaymentClient, hacer get(dataId), verificar estado (approved)
            // Obtener el external_reference (Usuario ID) y actualizar su fecha de suscripción.
        } else {
            log.debug("Evento ignorado (No es 'payment'): {}", tipo);
        }
    }
}