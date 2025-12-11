package org.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import okhttp3.*;

import org.app.model.IAPrompt;
import org.app.model.Language;
import org.app.model.requests.IAPropmptRequest;
import org.app.repository.IAPropmpRepository;
import org.app.repository.UserRepository;
import org.app.utils.LocalLog;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    // FAIL FAST
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

    public String promptLlamaTiny(IAPropmptRequest request) throws IOException {

        // Fail fast
        IAPrompt dbPrompt = handlePropmpts(request, iaPropmpRepository, userRepository);

    // Create the request body
    Map<String, Object> requestBodyMap = new HashMap<>();
    requestBodyMap.put("model", "tinyllama");
    requestBodyMap.put("prompt", request.getNewPrompt());
    requestBodyMap.put("stream", false);

        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        // Configura o cliente com timeouts mais longos
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)   // tempo para abrir conexão
                .writeTimeout(30, TimeUnit.SECONDS)     // tempo para enviar request
                .readTimeout(5, TimeUnit.MINUTES)       // tempo para esperar a resposta
                .build();

        // Cria a requisição HTTP
        Request httpRequest = new Request.Builder()
                .url("http://localhost:11434/api/generate")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        // Envia a requisição e processa a resposta
        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error in request: " + response.code() + " - " + response.message());
            }

            // Corpo da resposta
            String responseBody = response.body().string();

            // Extrai o campo "response" do JSON
            String llamaResponse = objectMapper.readTree(responseBody)
                    .path("response")
                    .asText();

            // Atualiza no banco
            dbPrompt.setResponseInLastIndex(llamaResponse);
            dbPrompt.updateLastUpdate();
            iaPropmpRepository.save(dbPrompt);

            return llamaResponse;
        }
    }

    public String llama3Response(IAPropmptRequest request) throws IOException {

        LocalLog.log("[llama3Response] Received from user "+ request.getIp() +" request: " + request.getNewPrompt());
        // Fail fast
        IAPrompt dbPrompt = handlePropmpts(request, iaPropmpRepository, userRepository);

        String instrucoes = "";
        switch (request.getLanguage()) {
            case PORTUGUESE -> instrucoes = "Responder em português.";
            default -> instrucoes = "Answer in English.";
        }
        LocalLog.log("[llama3Response] Instrucoes: " + instrucoes);

        // Create the request body
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "llama3");
        requestBodyMap.put("prompt", request.getNewPrompt()+instrucoes);
        requestBodyMap.put("stream", false);

        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        LocalLog.log("[llama3Response] Request body: " + requestBody);

        // Configura o cliente com timeouts mais longos
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)   // tempo para abrir conexão
                .writeTimeout(30, TimeUnit.SECONDS)     // tempo para enviar request
                .readTimeout(5, TimeUnit.MINUTES)       // tempo para esperar a resposta
                .build();

        // Cria a requisição HTTP
        Request httpRequest = new Request.Builder()
                .url("http://localhost:11434/api/generate")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        // Envia a requisição e processa a resposta
        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                LocalLog.logErr("[llama3Response] Error in request: " + response.code() + " - " + response.message());
                throw new IOException("Error in request: " + response.code() + " - " + response.message());
            }

            // Corpo da resposta
            String responseBody = response.body().string();

            // Extrai o campo "response" do JSON
            String llamaResponse = objectMapper.readTree(responseBody)
                    .path("response")
                    .asText();

            // Atualiza no banco
            dbPrompt.setResponseInLastIndex(llamaResponse);
            dbPrompt.updateLastUpdate();
            iaPropmpRepository.save(dbPrompt);

            LocalLog.log("[llama3Response] Responding to the user "+request.getIp());

            return llamaResponse;
        }
    }

    public void llama3StreamResponse(IAPropmptRequest request, HttpServletResponse response) throws IOException {

        // Fail fast
        IAPrompt dbPrompt = handlePropmpts(request, iaPropmpRepository, userRepository);

        // Força o flush automático no HTTP
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/event-stream");

        PrintWriter writer = response.getWriter();

        // Corpo da requisição
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "llama3");
        requestBodyMap.put("prompt", request.getNewPrompt());
        requestBodyMap.put("stream", true); // 🔥 streaming verdadeiro!

        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS) // 🔥 streaming nunca timeout
                .build();

        Request httpRequest = new Request.Builder()
                .url("http://localhost:11434/api/generate")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        try (Response ollamaResponse = client.newCall(httpRequest).execute()) {
            if (!ollamaResponse.isSuccessful()) {
                writer.write("event: error\ndata: Request failed " + ollamaResponse.code() + "\n\n");
                writer.flush();
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(ollamaResponse.body().byteStream()));

            String line;
            StringBuilder fullResponse = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                // Cada linha do Ollama STREAM é um JSON
                String token = objectMapper.readTree(line)
                        .path("response")
                        .asText();

                if (!token.isEmpty()) {
                    fullResponse.append(token);

                    // 🔥 Envia token imediatamente para o front
                    writer.write("data: " + token + "\n\n");
                    writer.flush();
                }

                boolean done = objectMapper.readTree(line).path("done").asBoolean(false);
                if (done) break;
            }

            // 🔥 Finaliza stream
            writer.write("event: end\ndata: done\n\n");
            writer.flush();

            // Salva no banco
            dbPrompt.setResponseInLastIndex(fullResponse.toString());
            dbPrompt.updateLastUpdate();
            iaPropmpRepository.save(dbPrompt);
        }
    }

    // MÉTODO UTILIZANDO LLAMA SERVER (ROCm / GPU)
    public String llamaServerResponse(IAPropmptRequest request) throws IOException {

        LocalLog.log("[LLAMA SERVER] Received from user " + request.getIp() +
                " request: " + request.getNewPrompt());

        // Fail fast + gerenciamento de prompts
        IAPrompt dbPrompt = handlePropmpts(request, iaPropmpRepository, userRepository);

        // Idioma
        String instrucoes = switch (request.getLanguage()) {
            case PORTUGUESE -> "Responda sempre em português.";
            default -> "Respond always in English.";
        };

        LocalLog.log("[LLAMA SERVER] Instrucoes: " + instrucoes);

        // Corpo OpenAI-Compatible
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "local-llama"); // apenas nominal, o servidor ignora
        requestBodyMap.put("stream", false);
        requestBodyMap.put("temperature", 0.7);

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", instrucoes),
                Map.of("role", "user", "content", request.getNewPrompt())
        );

        requestBodyMap.put("messages", messages);

        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        LocalLog.log("[LLAMA SERVER] Request body: " + requestBody);

        // Client otimizado para inferência em GPU
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();

        // Endpoint do llama-server (OpenAI-style)
        Request httpRequest = new Request.Builder()
                .url("http://localhost:1234/v1/chat/completions")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                LocalLog.logErr("[LLAMA SERVER] Error: " + response.code() +
                        " - " + response.message());
                throw new IOException("Error: " + response.code() + " - " + response.message());
            }

            String responseBody = response.body().string();

            // Padrão OpenAI:
            String answer = objectMapper.readTree(responseBody)
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();

            // Atualiza banco
            dbPrompt.setResponseInLastIndex(answer);
            dbPrompt.updateLastUpdate();
            iaPropmpRepository.save(dbPrompt);

            LocalLog.log("[LLAMA SERVER] Responding to user " + request.getIp());

            return answer;
        }
    }


    private String findGeminiExecutable() throws IOException {
    // 1. Check for GEMINI_PATH
    String geminiPath = System.getenv("GEMINI_PATH");
    if (geminiPath != null && !geminiPath.isEmpty()) {
        return geminiPath;
    }

    // 2. Try standard PATH
    try {
        ProcessBuilder pb = new ProcessBuilder("which", "gemini");
        Process p = pb.start();
        if (p.waitFor() == 0) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String path = reader.readLine();
                if (path != null && !path.isEmpty()) return path;
            }
        }
    } catch (Exception e) {
        LocalLog.log("Standard 'which gemini' failed. PATH is: " + System.getenv("PATH") + ". Trying nvm-aware fallback.");
    }

    // 3. Fallback: Source nvm.sh and then try which
    try {
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", ". /home/julianos/.config/nvm/nvm.sh && which gemini");
        pb.redirectErrorStream(true); // Combine stdout and stderr
        Process p = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        }

        if (p.waitFor() == 0) {
            String path = output.toString().trim();
            if (!path.isEmpty()) return path;
        }

        // If we are here, it failed. Throw with the output for debugging.
        throw new IOException("NVM-aware fallback failed. Output: " + output.toString());

    } catch (Exception e) {
        throw new IOException("Failed to execute nvm-aware fallback.", e);
    }
  }

//  public String geminiResponse(IAPropmptRequest prompt) throws IOException {
//    try {
//      String geminiPath = findGeminiExecutable();
//
//      // Escape single quotes in the prompt to prevent shell injection
//      String escapedPrompt = prompt.getNewPrompt().replace("'", "'\\''");
//      String command = String.format(". /home/julianos/.config/nvm/nvm.sh && %s -p '%s'", geminiPath, escapedPrompt);
//
//      ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
//      processBuilder.redirectErrorStream(true); // Redirect error stream to input stream
//
//      Process process = processBuilder.start();
//
//      StringBuilder output = new StringBuilder();
//      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//
//      String line;
//      while ((line = reader.readLine()) != null) {
//        output.append(line).append("\n");
//      }
//
//      int exitCode = process.waitFor();
//      if (exitCode != 0) {
//        String errorMessage = "Gemini CLI exited with error code " + exitCode + ". Output:\n" + output;
//        LocalLog.logErr(errorMessage);
//        throw new IOException(errorMessage);
//      }
//
//      String finalOutput = output.toString().replace("Loaded cached credentials.", "").trim();
//      return finalOutput;
//
//    } catch (IOException | InterruptedException e) {
//      if (e instanceof InterruptedException) {
//          Thread.currentThread().interrupt();
//      }
//      String errorMessage = "Failed to execute Gemini CLI: " + e.getMessage();
//      LocalLog.logErr(errorMessage);
//      throw new IOException(errorMessage, e);
//    }
//  }

    public String geminiResponse(IAPropmptRequest prompt) throws IOException {
        try {
            String geminiPath = "/home/soder/.nvm/versions/node/v22.14.0/bin/gemini";

            // Escape single quotes in the prompt to prevent shell injection
            String escapedPrompt = prompt.getNewPrompt().replace("'", "'\\''");
            String command = String.format(". /home/soder/.nvm/nvm.sh && %s -p '%s'", geminiPath, escapedPrompt);

            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
            processBuilder.redirectErrorStream(true); // Redirect error stream to input stream

            Process process = processBuilder.start();

            // By closing the process's output stream, we signal to the child process
            // that it should not expect any input. This prevents the child process from
            // hanging indefinitely while waiting for input that will never be sent.
            process.getOutputStream().close();

            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String errorMessage = "Gemini CLI exited with error code " + exitCode + ". Output:\n" + output;
                LocalLog.logErr(errorMessage);
                throw new IOException(errorMessage);
            }

            String finalOutput = output.toString().replace("Loaded cached credentials.", "").trim();
            return finalOutput;

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            String errorMessage = "Failed to execute Gemini CLI: " + e.getMessage();
            LocalLog.logErr(errorMessage);
            throw new IOException(errorMessage, e);
        }
    }
}
