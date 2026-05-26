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
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Transaccion;
import com.budgetmap.model.enums.EstadoTransaccion;
import com.budgetmap.repository.TransaccionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Slf4j
@Service
public class PasarelaService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${budgetmap.url.webhook}")
    private String webhookUrl;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
        log.info("Mercado Pago configurado correctamente con el Access Token.");
    }

    public String crearPreferenciaDePago(String tituloPlan, BigDecimal precioMensual, String referenciaInterna) {
        log.info("Iniciando creación de preferencia de pago: {} - Ref: {}", tituloPlan, referenciaInterna);
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

            log.info("Preferencia de pago creada exitosamente. ID: {}", preference.getId());
            return preference.getInitPoint();

        } catch (MPException | MPApiException e) {
            log.error("Error crítico al comunicarse con la pasarela de Mercado Pago", e);
            throw new IllegalStateException("Error al comunicarse con la pasarela de pagos", e);
        }
    }

    @Transactional
    public void procesarNotificacionDePago(Long paymentId) {
        log.info("Recibiendo webhook de Mercado Pago para el Payment ID: {}", paymentId);
        try {
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(paymentId);

            String referenciaInterna = payment.getExternalReference();
            String estadoPago = payment.getStatus(); 

            Transaccion transaccion = transaccionRepository.findByReferenciaPago(referenciaInterna)
                    .orElseThrow(() -> {
                        log.error("Transacción no encontrada a partir del webhook: {}", referenciaInterna);
                        return new ResourceNotFoundException("Transacción no encontrada: " + referenciaInterna);
                    });

            if ("approved".equals(estadoPago)) {
                log.info("Pago APROBADO para la transacción: {}", referenciaInterna);
                transaccion.setEstado(EstadoTransaccion.EXITOSO);
                var usuario = transaccion.getUsuario();
                usuario.setFechaFinSuscripcion(LocalDateTime.now().plusDays(30));
            } else {
                log.warn("Pago NO APROBADO. Estado reportado: {} para transacción: {}", estadoPago, referenciaInterna);
                transaccion.setEstado(EstadoTransaccion.FALLIDO);
            }

            transaccionRepository.save(transaccion);

        } catch (MPException | MPApiException e) {
            log.error("Error validando el pago con Mercado Pago para ID {}", paymentId, e);
        }
    }
}