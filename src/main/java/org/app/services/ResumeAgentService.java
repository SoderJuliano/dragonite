package org.app.services;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static org.app.config.SecretManager.getSecret;

@Service
public class ResumeAgentService {
    private static final String BASE_URL = "https://api.aimlapi.com/v1";
    private static final Properties properties = new Properties();

    // Carrega as propriedades do arquivo de segredos
    static {
        String path = "/home/soder/Área de trabalho/app/secrets.txt"; // Caminho do arquivo de segredos
        try (FileInputStream input = new FileInputStream(path)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load secrets file", e);
        }
    }

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateResume(String userPrompt, String systemPrompt) throws IOException {
        String apiKey = getSecret("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API Key not found in secrets file");
        }

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "mistralai/Mistral-7B-Instruct-v0.2");
        requestBodyMap.put("messages", List.of(systemMessage, userMessage));
        requestBodyMap.put("temperature", 0.5);
        requestBodyMap.put("max_tokens", 512);

        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
        Request request = new Request.Builder()
                .url(BASE_URL + "/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body().string();
                throw new IOException("Erro na solicitação: " + response.code() + " - " + response.message() + " - " + errorBody);
            }
            String responseBody = response.body().string();
            return objectMapper.readTree(responseBody)
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        }
    }
}