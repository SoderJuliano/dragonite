package org.app.controller;

import org.app.Exceptions.BadRequestException;
import org.app.model.entity.User;
import org.app.model.requests.IAPropmptRequest;
import org.app.services.IAService;
import org.app.services.ResumeAgentService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@CrossOrigin
public class IAController {
    private final IAService iaService;
    private final ResumeAgentService resumeAgentService;

    public IAController(IAService iaService, ResumeAgentService resumeAgentService) {
        this.iaService = iaService;
        this.resumeAgentService = resumeAgentService;
    }

    @PostMapping("/improve-text")
    public String generate(@RequestBody IAPropmptRequest prompt) throws IOException {
        if (prompt.isAgent()) {
            throw new BadRequestException("Call /generate-cv endpoint insted!");
        }

        return resumeAgentService.improveText(prompt);
    }

    @PostMapping("/generate-cv")
    public User generateCv(@RequestBody IAPropmptRequest prompt) {
        if (!prompt.isAgent()) {
            throw new BadRequestException("Call /generate endpoint insted!");
        }
        return resumeAgentService.generateResume(prompt);
    }

        @PostMapping("/llama3")
    public String generateTextWithLlama(@RequestBody IAPropmptRequest prompt) throws IOException {
        if (prompt.isAgent()) {
            throw new BadRequestException("Call /generate-cv endpoint insted!");
        }
        return iaService.llama3Response(prompt);
    }

    @PostMapping("/gemini")
    public String generateTextWithGemini(@RequestBody IAPropmptRequest prompt) throws IOException {
        return iaService.geminiResponse(prompt);
    }
}