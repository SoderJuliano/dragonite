package org.app.model.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.app.model.Language;

public class IAPropmptRequest {

    @Schema(description = "Provide a brief and clear description of the job you want to get.", example = "Software developer with experience in Java and Spring Boot.")
    @NotNull
    private String newPrompt;

    @Schema(description = "The IP address of the user", example = "192.168.1.1")
    @NotNull
    private String ip;

    @Schema(description = "Indicates if the request is from an agent", example = "false")
    private boolean agent;

    @Schema(example = "teste@teste.com")
    private String email;

//    @Schema(example = "")
    private Language language;

    public IAPropmptRequest(String ip, boolean agent, String userEmail, String newPrompt) {
        this.agent = agent;
        this.ip = ip;
        this.newPrompt = newPrompt;
    }

    public String getNewPrompt() {
        return newPrompt;
    }

    public void setNewPrompt(String newPrompt) {
        this.newPrompt = newPrompt;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isAgent() {
        return agent;
    }

    public void setAgent(boolean agent) {
        this.agent = agent;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
