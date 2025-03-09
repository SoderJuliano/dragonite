package org.app.controller;

import org.app.Exceptions.BadRequestException;
import org.app.model.entity.User;
import org.app.model.requests.IAPropmptRequest;
import org.app.services.HuggingFaceService;
import org.app.services.IAService;
import org.app.services.ResumeAgentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class IAController {
    private final IAService IAService;
    private final ResumeAgentService resumeAgentService;
    private final HuggingFaceService huggingFaceService;

    public IAController(IAService IAService, ResumeAgentService resumeAgentService, HuggingFaceService huggingFaceService) {
        this.IAService = IAService;
        this.resumeAgentService = resumeAgentService;
        this.huggingFaceService = huggingFaceService;
    }

    @PostMapping("/generate")
    public String generate(@RequestBody IAPropmptRequest prompt) throws IOException {
        if (prompt.isAgent()) {
            throw new BadRequestException("Call /generate-cv endpoint insted!");
        }
//        return IAService.generateText(prompt);
        String fullPrompt = "Você é um agente que resume e melhora textos de maneira profissional, sem utilizar quebra de linha ou caracteres especiais." +
                "Melhore esse texto de maneira profissional, de no máximo 30 palavras, texto: " + prompt.getNewPrompt();

        return huggingFaceService.generateText(fullPrompt);
    }

    @PostMapping("/generate-cv")
    public User generateCv(@RequestBody IAPropmptRequest prompt) {
        if (!prompt.isAgent()) {
            throw new BadRequestException("Call /generate endpoint insted!");
        }
        return resumeAgentService.generateResume(prompt);
    }
}