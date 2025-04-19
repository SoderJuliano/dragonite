package org.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.model.entity.Payment;
import org.app.repository.PaymentRepository;
import org.app.services.UserService;
import org.app.utils.PayPalWebhookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private PayPalWebhookValidator payPalWebhookValidator;

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestBody Payment request) {
        Payment payment = new Payment();
        payment.setPaymentId("PENDENTE-" + UUID.randomUUID());
        payment.setUserId(request.getUserId());
        payment.setEmail(request.getEmail());
        payment.setAmount(request.getAmount());
        payment.setStatus("PENDENTE");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);  // Salva no MongoDB
        return ResponseEntity.ok(payment.getPaymentId());
    }

    @GetMapping("/history/{userId}")
    public List<Payment> getPayments(@PathVariable String userId) {
        return paymentRepository.findByUserId(userId);  // Consulta no MongoDB
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("PAYPAL-SIGNATURE") String signature) throws JsonProcessingException {
        // 1. Valida a assinatura (exemplo simplificado)
        if (!payPalWebhookValidator.validateWebhook(payload, signature)) {
            return ResponseEntity.status(403).body("Assinatura inválida");
        }

        // 2. Extrai o ID do pagamento
        JsonNode json = new ObjectMapper().readTree(payload);
        String paypalPaymentId = json.get("resource").get("id").asText();

        // 3. Atualiza o status no MongoDB
        Payment payment = paymentRepository.findByPaymentId(paypalPaymentId);
        payment.setStatus("CONCLUIDO");
        paymentRepository.save(payment);

        // 4. Libera acesso ao usuário (ex.: atualiza seu serviço de usuários)
        userService.grantPremiumAccess(payment.getUserId());

        return ResponseEntity.ok("Webhook processado");
    }
}