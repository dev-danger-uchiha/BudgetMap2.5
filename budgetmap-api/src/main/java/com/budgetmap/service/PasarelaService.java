package com.budgetmap.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import com.budgetmap.model.Transaccion;
import com.budgetmap.model.enums.EstadoTransaccion;
import com.budgetmap.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class PasarelaService {

    // ¡Ahora sí está adentro de la clase!
    @Autowired
    private TransaccionRepository transaccionRepository;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${budgetmap.url.webhook}")
    private String webhookUrl;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    // Método 1: Genera el Link de Pago
    public String crearPreferenciaDePago(String tituloPlan, BigDecimal precioMensual, String referenciaInterna) {
        try {
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .title(tituloPlan)
                    .quantity(1)
                    .unitPrice(precioMensual)
                    .currencyId("COP")
                    .build();
            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .externalReference(referenciaInterna) 
                    .notificationUrl(webhookUrl)          
                    .backUrls(PreferenceBackUrlsRequest.builder()
                            .success("http://localhost:3000/pago-exitoso") 
                            .failure("http://localhost:3000/pago-fallido")
                            .pending("http://localhost:3000/pago-pendiente")
                            .build())
                    .autoReturn("approved")
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return preference.getInitPoint();

        } catch (MPException | MPApiException e) {
            throw new RuntimeException("Error al comunicarse con la pasarela de pagos", e);
        }
    }

    // Método 2: El Webhook (Atiende la llamada de Mercado Pago)
    @Transactional
    public void procesarNotificacionDePago(Long paymentId) {
        try {
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(paymentId);

            String referenciaInterna = payment.getExternalReference();
            String estadoPago = payment.getStatus(); 

            Transaccion transaccion = transaccionRepository.findByReferenciaPago(referenciaInterna)
                    .orElseThrow(() -> new RuntimeException("Transacción no encontrada: " + referenciaInterna));

            if ("approved".equals(estadoPago)) {
                transaccion.setEstado(EstadoTransaccion.EXITOSO);
                var usuario = transaccion.getUsuario();
                usuario.setFechaFinSuscripcion(LocalDateTime.now().plusDays(30));
            } else {
                transaccion.setEstado(EstadoTransaccion.FALLIDO);
            }

            transaccionRepository.save(transaccion);

        } catch (MPException | MPApiException e) {
            System.err.println("Error validando el pago con Mercado Pago: " + e.getMessage());
        }
    }
}