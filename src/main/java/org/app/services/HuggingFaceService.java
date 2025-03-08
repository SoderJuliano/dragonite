package org.app.services;

import okhttp3.*;

import java.io.IOException;

import static org.app.config.SecretManager.getSecret;

public class HuggingFaceService {
    private static final String API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2";
    private static final String API_KEY = getSecret("HUGGING_FACE");

    private final OkHttpClient client = new OkHttpClient();

    public String generateResume(String userPrompt, String systemPrompt) throws IOException {
        HuggingFaceService huggingFaceService = new HuggingFaceService();
        return huggingFaceService.generateText(userPrompt);
    }

    public String generateText(String prompt) throws IOException {
        // Cria o corpo da requisição
        String json = "{\"inputs\": \"" + prompt + "\"}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        // Cria a requisição
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        // Executa a requisição
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro na requisição: " + response.code() + " - " + response.message());
            }
            return response.body().string();
        }
    }
}
