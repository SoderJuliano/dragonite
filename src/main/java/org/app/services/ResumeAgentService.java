package org.app.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.app.model.Language;
import org.app.model.entity.User;
import org.app.model.requests.IAPropmptRequest;
import org.app.repository.IAPropmpRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static org.app.config.SecretManager.getSecret;
import static org.app.utils.AgentServiceUtil.handlePropmpts;
import static org.app.utils.Commons.extractJsonFromString;

@Service
public class ResumeAgentService {
    public static final String API_KEY0 = getSecret("OPENAI_API_KEY");
    public static final String API_KEY1 = getSecret("aimlapi.com_KEY1");
    public static final String API_KEY2 = getSecret("aimlapi.com_KEY2");
    public static final String API_KEY3 = getSecret("aimlapi.com_KEY3");
    public static final String API_KEY4 = getSecret("aimlapi.com_KEY4");

    private ArrayList<String> keys = new ArrayList<>();

    public static final String IN_ENGLISH = "in English.";
    private static final String BASE_URL = "https://api.aimlapi.com/v1";
    private static final Properties properties = new Properties();
    public static final String FILL_WITH_DATA = "Fill with data:";
    public static final String PREENCHA_COM_DADOS_CURTOS = "Preencha com dados curtos:";
    public static final String NAME_PROFESSION_RESUME_CONTACT_EMAIL_PHONE_ADDRESS = " {\"name\":\"\",\"profession\":\"\",\"resume\":\"\",\"contact\":{\"email\":[],\"phone\":[],\"address\":\"\"}}";
    public static final String COMPETENCE_USER_EXPERIENCES_ABILITY =
            " {\"competence\":[],\"userExperiences\":[{\"position\":\"\",\"company\":\"\",\"dateHired\":\"\",\"dateFired\":\"\",\"description\":\"\"}],\"ability\":\"\"}";
    public static final String SPOKEN_LANGUAGES_LEVEL_DETAILS_OTHER_INFOS_LANGUAGE = " {\"spokenLanguages\":[{\"level\":\"\",\"details\":\"\"}],\"otherInfos\":[],\"language\":\"\"}";
    public static final String EM_PORTUGUES = "em português";
    private final IAPropmpRepository iaPropmpRepository;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumeAgentService(IAPropmpRepository iaPropmpRepository) {
        this.iaPropmpRepository = iaPropmpRepository;

        keys.add(API_KEY0);
        keys.add(API_KEY1);
        keys.add(API_KEY2);
        keys.add(API_KEY3);
        keys.add(API_KEY4);
    }

    public User generateResume(IAPropmptRequest request) {

        handlePropmpts(request, iaPropmpRepository);

        ArrayList<String> systemPrompts = new ArrayList<>();
        ArrayList<String> userPrompts = new ArrayList<>();

        if(request.getLanguage() == Language.ENGLISH) {
            String systemPrompt1 = FILL_WITH_DATA + NAME_PROFESSION_RESUME_CONTACT_EMAIL_PHONE_ADDRESS;
            String systemPrompt2 = FILL_WITH_DATA + COMPETENCE_USER_EXPERIENCES_ABILITY;
            String systemPrompt3 = FILL_WITH_DATA + SPOKEN_LANGUAGES_LEVEL_DETAILS_OTHER_INFOS_LANGUAGE;
            systemPrompts.add(systemPrompt1);
            systemPrompts.add(systemPrompt2);
            systemPrompts.add(systemPrompt3);

            String userPrompt1 = "Personal data for " + request.getNewPrompt() + IN_ENGLISH;
            String userPrompt2 = "Skills and experiences for " + request.getNewPrompt() + IN_ENGLISH;
            String userPrompt3 = "Languages and other infos for" + request.getNewPrompt() + IN_ENGLISH;
            userPrompts.add(userPrompt1);
            userPrompts.add(userPrompt2);
            userPrompts.add(userPrompt3);
        }else {
            String systemPrompt1 = PREENCHA_COM_DADOS_CURTOS + NAME_PROFESSION_RESUME_CONTACT_EMAIL_PHONE_ADDRESS;
            String systemPrompt2 = PREENCHA_COM_DADOS_CURTOS + COMPETENCE_USER_EXPERIENCES_ABILITY;
            String systemPrompt3 = PREENCHA_COM_DADOS_CURTOS + SPOKEN_LANGUAGES_LEVEL_DETAILS_OTHER_INFOS_LANGUAGE;
            systemPrompts.add(systemPrompt1);
            systemPrompts.add(systemPrompt2);
            systemPrompts.add(systemPrompt3);

            String userPrompt1 = "Dados pessoais para " + request.getNewPrompt() + EM_PORTUGUES;
            String userPrompt2 = "Competências e experiências para " + request.getNewPrompt() + EM_PORTUGUES;
            String userPrompt3 = "Idiomas e outras infos para " + request.getNewPrompt() + EM_PORTUGUES;
            userPrompts.add(userPrompt1);
            userPrompts.add(userPrompt2);
            userPrompts.add(userPrompt3);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String result1 = generateResume(userPrompts.get(0), systemPrompts.get(0));
            String result2 = generateResume(userPrompts.get(1), systemPrompts.get(1));
            String result3 = generateResume(userPrompts.get(2), systemPrompts.get(2));

            ObjectNode mergedJson = objectMapper.createObjectNode();
            mergedJson.setAll((ObjectNode) objectMapper.readTree(result1));
            mergedJson.setAll((ObjectNode) objectMapper.readTree(result2));
            mergedJson.setAll((ObjectNode) objectMapper.readTree(result3));

            // Ajustar otherInfos para ser uma lista de strings
            if (mergedJson.has("otherInfos")) {
                ArrayNode otherInfosArray = (ArrayNode) mergedJson.get("otherInfos");
                ArrayNode newOtherInfosArray = objectMapper.createArrayNode();
                for (JsonNode item : otherInfosArray) {
                    if (item.isObject()) {
                        String key = item.get("key").asText();
                        String value = item.get("value").asText();
                        newOtherInfosArray.add(key + ": " + value);
                    } else {
                        newOtherInfosArray.add(item);
                    }
                }
                mergedJson.set("otherInfos", newOtherInfosArray);
            }

            String finalJson = objectMapper.writeValueAsString(mergedJson);
            System.out.println("Currículo Completo: " + finalJson);

            return objectMapper.readValue(finalJson, User.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public String generateResume(String userPrompt, String systemPrompt) throws IOException {
        if (keys.isEmpty()) {
            throw new RuntimeException("API Key not found in secrets file");
        }

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt+"only the JSON");

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "mistralai/Mistral-7B-Instruct-v0.2");
        requestBodyMap.put("messages", List.of(systemMessage, userMessage));
        requestBodyMap.put("temperature", 0.5);
        requestBodyMap.put("max_tokens", 512);

        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        int retryCount = 0;
        int keyIndex = 0;
        IOException lastException = null;

        while (retryCount < 3) {
            String currentKey = keys.get(keyIndex);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/chat/completions")
                    .addHeader("Authorization", "Bearer " + currentKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body().string();
                    if (response.code() == 429) {
                        // Limite atingido, tenta a próxima chave
                        keyIndex = (keyIndex + 1) % keys.size();
                        retryCount++;
                        lastException = new IOException("Limite de chamadas atingido para a chave " + currentKey + ". Tentando a próxima chave.");
                        continue;
                    } else {
                        throw new IOException("Erro na solicitação: " + response.code() + " - " + response.message() + " - " + errorBody);
                    }
                }
                String responseBody = extractJsonFromString(response.body().string());
                return objectMapper.readTree(responseBody)
                        .path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();
            } catch (IOException e) {
                retryCount++;
                lastException = e;
            }
        }

        // Se chegou aqui, significa que houve 3 erros consecutivos
        throw new IOException("Falha após 3 tentativas. Último erro: " + (lastException != null ? lastException.getMessage() : "Desconhecido"));
    }
}