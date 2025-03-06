package org.app.services;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import org.app.config.SecretManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAIService {
    private final OpenAiService openAiService;

    public OpenAIService() {
        String apiKey = SecretManager.getSecret("openai.api-key");
        this.openAiService = new OpenAiService(apiKey);
    }

    public String generateText(String prompt) {
        CompletionRequest request = CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt(prompt)
                .maxTokens(100)
                .build();
        return openAiService.createCompletion(request).getChoices().get(0).getText();
    }
}