package org.app.services;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.app.config.SecretManager.getSecret;

@Service
public class HuggingFaceService {
    private static final String API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2";
    private static final String API_KEY = getSecret("HUGGING_FACE");

    private final OkHttpClient client = new OkHttpClient();

    public String generateText(String prompt) throws IOException {
        // Cria o corpo da requisição com o prompt diretamente
        String json = "{\"inputs\": \"" + prompt + "\"}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        // Cria a requisição
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        // Executa a requisição
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Erro na requisição: " + response.code() + " - " + response.message());
            }
            return extractModelResponse(response.body().string(), prompt);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para extrair apenas a resposta do modelo
    private String extractModelResponse(String responseBody, String prompt) {
        // Remove a parte do prompt da resposta
        int promptIndex = responseBody.indexOf(prompt);
        if (promptIndex != -1) {
            return responseBody.substring(promptIndex + prompt.length()).trim();
        }
        return responseBody; // Retorna a resposta completa se o prompt não for encontrado
    }
}
