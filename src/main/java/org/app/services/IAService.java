package org.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.app.model.requests.IAPropmptRequest;
import org.app.repository.IAPropmpRepository;
import org.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.app.config.SecretManager.getSecret;
import static org.app.utils.AgentServiceUtil.handlePropmpts;

@Service
public class IAService {
    private static final String BASE_URL = "https://api.aimlapi.com/v1";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final IAPropmpRepository iaPropmpRepository;
    private final UserRepository userRepository;

    public IAService(IAPropmpRepository iaPropmpRepository, UserRepository userRepository) {
        this.iaPropmpRepository = iaPropmpRepository;
        this.userRepository = userRepository;
    }

    public String generateText(IAPropmptRequest prompt) throws IOException {

//        FAIL FAST
        handlePropmpts(prompt, iaPropmpRepository, userRepository);

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

            return objectMapper.readTree(responseBody)
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        }
    }

    public String llama3Response(IAPropmptRequest request) throws IOException {

//        Fail fast
        handlePropmpts(request, iaPropmpRepository, userRepository);


        // Create the request body
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "llama3");
        requestBodyMap.put("prompt", request.getNewPrompt());
        requestBodyMap.put("stream", false);

        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        // Create the HTTP request
        Request httpRequest = new Request.Builder()
                .url("http://localhost:11434/api/generate")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        // Send the request and process the response
        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error in request: " + response.code() + " - " + response.message());
            }

            // Process the response
            String responseBody = response.body().string();

            // Assuming the response is a JSON object with a "response" field
            return objectMapper.readTree(responseBody)
                    .path("response")
                    .asText();
        }
    }

}