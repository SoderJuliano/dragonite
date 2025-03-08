package org.app.model;

import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Document(collection = "ia_prompts")
public class IAPrompt {
    @Id
    private ObjectId _id;
    @NotNull
    private String ip;
    private ArrayList<String> prompts;
    private boolean agent;
    private String userEmail;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public IAPrompt() {
        // empty
    }

    public IAPrompt(ObjectId _id, String ip, ArrayList<String> prompts, boolean agent, String userEmail,
                    LocalDateTime createDate, LocalDateTime updateDate) {
        this._id = _id;
        this.ip = ip;
        this.prompts = prompts;
        this.agent = agent;
        this.userEmail = userEmail;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public IAPrompt(String ip, ArrayList<String> prompts, boolean agent, String userEmail, LocalDateTime updateDate) {
        this.ip = ip;
        this.prompts = prompts;
        this.agent = agent;
        this.userEmail = userEmail;
        this.updateDate = updateDate;
    }

    public IAPrompt(String ip, ArrayList<String> prompts, boolean agent, String userEmail,
                    LocalDateTime createDate , LocalDateTime updateDate) {
        this.ip = ip;
        this.prompts = prompts;
        this.agent = agent;
        this.userEmail = userEmail;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ArrayList<String> getPrompts() {
        return prompts;
    }

    public void setPrompts(ArrayList<String> prompts) {
        this.prompts = prompts;
    }

    public boolean isAgent() {
        return agent;
    }

    public void setAgent(boolean agent) {
        this.agent = agent;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getLastUpdate() {
        return this.updateDate;
    }

    public void updateLastUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}
