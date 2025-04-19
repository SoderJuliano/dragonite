package org.app.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Base64;

@Component
public class PayPalWebhookValidator {

    private final String paypalApiUrl;
    private final String webhookId;
    private final String clientId;
    private final String clientSecret;
    private final OkHttpClient httpClient;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public PayPalWebhookValidator(
            @Value("${paypal.api.url}") String paypalApiUrl,
            @Value("${paypal.webhook.id}") String webhookId,
            @Value("${paypal.client.id}") String clientId,
            @Value("${paypal.client.secret}") String clientSecret) {
        this.paypalApiUrl = paypalApiUrl;
        this.webhookId = webhookId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.httpClient = new OkHttpClient();
    }

    public boolean validateWebhook(String payload, String signatureHeader) {
        try {
            SignatureComponents components = parseSignatureHeader(signatureHeader);
            Request request = buildVerificationRequest(payload, components);
            return executeVerification(request);
        } catch (IllegalArgumentException e) {
            logError("Invalid signature format", e);
            return false;
        } catch (IOException e) {
            logError("Error verifying webhook", e);
            return false;
        }
    }

    private SignatureComponents parseSignatureHeader(String signatureHeader) {
        String[] parts = signatureHeader.split(",");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid signature header format");
        }

        return new SignatureComponents(
                extractComponentValue(parts[0], "alg"),
                extractComponentValue(parts[1], "cert_url"),
                extractComponentValue(parts[2], "transmission_id"),
                extractComponentValue(parts[3], "transmission_sig"),
                extractComponentValue(parts[4], "transmission_time")
        );
    }

    private String extractComponentValue(String part, String key) {
        String[] keyValue = part.split("=");
        if (keyValue.length != 2 || !keyValue[0].equals(key)) {
            throw new IllegalArgumentException("Invalid signature component: " + part);
        }
        return keyValue[1];
    }

    private Request buildVerificationRequest(String payload, SignatureComponents components) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("auth_algo", components.authAlgo());
        requestBody.addProperty("cert_url", components.certUrl());
        requestBody.addProperty("transmission_id", components.transmissionId());
        requestBody.addProperty("transmission_sig", components.transmissionSig());
        requestBody.addProperty("transmission_time", components.transmissionTime());
        requestBody.addProperty("webhook_id", webhookId);
        requestBody.add("webhook_event", JsonParser.parseString(payload));

        String auth = clientId + ":" + clientSecret;
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());

        return new Request.Builder()
                .url(paypalApiUrl + "/v1/notifications/verify-webhook-signature")
                .addHeader("Authorization", authHeader)
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
    }

    private boolean executeVerification(Request request) throws IOException {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }

            JsonObject responseBody = JsonParser.parseString(response.body().string()).getAsJsonObject();
            return "SUCCESS".equals(responseBody.get("verification_status").getAsString());
        }
    }

    private void logError(String message, Exception e) {
        // Implement proper logging (SLF4J, etc.)
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }

    private record SignatureComponents(
            String authAlgo,
            String certUrl,
            String transmissionId,
            String transmissionSig,
            String transmissionTime
    ) {}
}