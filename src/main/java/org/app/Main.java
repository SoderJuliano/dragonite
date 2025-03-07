package org.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.app.config.SecretManager;
import org.app.services.OpenAIService;
import org.app.services.ResumeAgentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SecretManager.setMongoURI();
        SpringApplication.run(Main.class, args);

        ResumeAgentService resumeAgent = new ResumeAgentService();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String systemPrompt1 = "Preencha com dados curtos: {\"name\":\"\",\"profession\":\"\",\"resume\":\"\",\"contact\":{\"email\":[],\"phone\":[],\"address\":\"\"}}";
            String userPrompt1 = "Dados pessoais para Engenheiro de Software em português";
            String result1 = resumeAgent.generateResume(userPrompt1, systemPrompt1);

            String systemPrompt2 = "Preencha com dados curtos: {\"competence\":[],\"userExperiences\":[],\"ability\":\"\"}";
            String userPrompt2 = "Competências e experiências para Engenheiro de Software em português";
            String result2 = resumeAgent.generateResume(userPrompt2, systemPrompt2);

            String systemPrompt3 = "Preencha com dados curtos: {\"spokenLanguages\":[{\"level\":\"\",\"details\":\"\"}],\"otherInfos\":[],\"language\":\"\"}";
            String userPrompt3 = "Idiomas e outras infos para Engenheiro de Software em português";
            String result3 = resumeAgent.generateResume(userPrompt3, systemPrompt3);

            ObjectNode mergedJson = objectMapper.createObjectNode();
            mergedJson.setAll((ObjectNode) objectMapper.readTree(result1));
            mergedJson.setAll((ObjectNode) objectMapper.readTree(result2));
            mergedJson.setAll((ObjectNode) objectMapper.readTree(result3));

            String finalJson = objectMapper.writeValueAsString(mergedJson);
            System.out.println("Currículo Completo: " + finalJson);

            // Combine os resultados manualmente ou com um merge de JSON
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}