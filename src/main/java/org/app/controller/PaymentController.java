package org.app.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.app.model.entity.Payment;
import org.app.repository.PaymentRepository;
import org.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import okhttp3.FormBody;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserService userService;

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.api.url}")
    private String paypalApiUrl;

    private final OkHttpClient httpClient = new OkHttpClient();

//    Meu app não esta com ip fixo nem com host fixo, então é impossível
//    cadastrar um host para webhook por enquanto
//    @Autowired
//    private PayPalWebhookValidator payPalWebhookValidator;

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

    @GetMapping("/confirm/{paymentId}")
    public ResponseEntity<String> confirmPayment(@PathVariable String paymentId) throws IOException {
        String accessToken = getAccessToken();

        Request request = new Request.Builder()
                .url(paypalApiUrl + "/v2/checkout/orders/" + paymentId)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return ResponseEntity.status(response.code()).body("Erro ao consultar PayPal");
            }

            String body = response.body().string();
            JsonNode json = new ObjectMapper().readTree(body);

            String status = json.get("status").asText();  // Ex: "COMPLETED"
            if (!"COMPLETED".equalsIgnoreCase(status)) {
                return ResponseEntity.ok("Pagamento ainda não concluído. Status: " + status);
            }

            // Atualiza seu banco de dados
            Payment payment = paymentRepository.findByPaymentId(paymentId);
            if (payment == null) {
                return ResponseEntity.status(404).body("Pagamento não encontrado");
            }

            payment.setStatus("CONCLUIDO");
            paymentRepository.save(payment);

            // Libera acesso
            userService.grantPremiumAccess(payment.getUserId());

            return ResponseEntity.ok("Pagamento confirmado e acesso liberado!");
        }
    }

    private String getAccessToken() throws IOException {
        String credential = Credentials.basic(clientId, clientSecret);

        okhttp3.RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();

        Request request = new Request.Builder()
                .url(paypalApiUrl + "/v1/oauth2/token")
                .header("Authorization", credential)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            JsonNode json = new ObjectMapper().readTree(responseBody);
            return json.get("access_token").asText();
        }
    }
}