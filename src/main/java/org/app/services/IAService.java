package org.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.app.Exceptions.NotFoundException;
import org.app.Exceptions.RateLimitExceededException;
import org.app.model.IAPrompt;
import org.app.model.requests.IAPropmptRequest;
import org.app.repository.IAPropmpRepository;
import org.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        IAPrompt dbPrompt = handlePropmpts(prompt, iaPropmpRepository, userRepository);

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
                dbPrompt.setResponseInLastIndex("Erro na solicitação: " + response.code() + " - " + response.message());
                dbPrompt.updateLastUpdate();
                iaPropmpRepository.save(dbPrompt);
                throw new IOException("Erro na solicitação: " + response.code() + " - " + response.message());
            }

            // Processa a resposta
            String responseBody = response.body().string();

            String serviceResponse = objectMapper.readTree(responseBody)
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            dbPrompt.setResponseInLastIndex(serviceResponse);
            iaPropmpRepository.save(dbPrompt);
            return serviceResponse;
        }
    }

    public String llama3Response(IAPropmptRequest request) throws IOException {

//        Fail fast
        IAPrompt dbPrompt = handlePropmpts(request, iaPropmpRepository, userRepository);


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
            String llamaResponse = objectMapper.readTree(responseBody)
                    .path("response")
                    .asText();

            dbPrompt.setResponseInLastIndex(llamaResponse);
            dbPrompt.updateLastUpdate();
            iaPropmpRepository.save(dbPrompt);
            return llamaResponse;
        }
    }

    private String findGeminiExecutable() throws IOException {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("which", "gemini");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String path = reader.readLine();

            int exitCode = process.waitFor();
            if (exitCode != 0 || path == null || path.isEmpty()) {
                throw new IOException("Gemini CLI not found in PATH. Please ensure it is installed and accessible.");
            }

            return path;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Failed to find Gemini CLI", e);
        }
    }

    public String geminiResponse(IAPropmptRequest prompt) throws IOException {
        try {
            String geminiPath = findGeminiExecutable();
            ProcessBuilder processBuilder = new ProcessBuilder(geminiPath, "-p", prompt.getNewPrompt());
            processBuilder.redirectErrorStream(true); // Redirect error stream to input stream

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Gemini CLI exited with error code " + exitCode + ". Output:\n" + output);
            }

            String finalOutput = output.toString().replace("Loaded cached credentials.", "").trim();
            return finalOutput;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to execute Gemini CLI", e);
        }
    }
}