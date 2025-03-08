package org.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.app.model.requests.IAPropmptRequest;
import org.app.repository.IAPropmpRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static org.app.config.SecretManager.getSecret;
import static org.app.utils.AgentServiceUtil.handlePropmpts;

@Service
public class IAService {
    private static final String BASE_URL = "https://api.aimlapi.com/v1";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final IAPropmpRepository iaPropmpRepository;

    public IAService(IAPropmpRepository iaPropmpRepository) {
        this.iaPropmpRepository = iaPropmpRepository;
    }

    public String generateText(IAPropmptRequest prompt) throws IOException {

        String apiKey = getSecret("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API Key not found in secrets file");
        }

        // Cria o corpo da solicitação no formato esperado pela AIML API
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt.getNewPrompt());

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "mistralai/Mistral-7B-Instruct-v0.2");
        requestBodyMap.put("messages", Collections.singletonList(message));
        requestBodyMap.put("temperature", 0.7);
        requestBodyMap.put("max_tokens", 256);

        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        // Cria a solicitação HTTP
        Request request = new Request.Builder()
                .url(BASE_URL + "/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        // Envia a solicitação e processa a resposta
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro na solicitação: " + response.code() + " - " + response.message());
            }

            // Processa a resposta
            String responseBody = response.body().string();

            handlePropmpts(prompt, iaPropmpRepository);

            return objectMapper.readTree(responseBody)
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        }
    }
}