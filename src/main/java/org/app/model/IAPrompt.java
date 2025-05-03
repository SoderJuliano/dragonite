package org.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Document(collection = "ia_prompts")
@CompoundIndex(name = "ip_blocked_idx", def = "{'ip': 1, 'blocked': 1}")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IAPrompt {
    @Id
    private ObjectId _id;
    @NotNull
    private String ip;
    private ArrayList<Prompt> prompts;
    private boolean agent;
    private String userEmail;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    private boolean blocked;
    private LocalDateTime blockedUntil;
    private int requestCount;
    private LocalDateTime lastRequestDate;

    public IAPrompt() {
        // empty
    }

    public IAPrompt(ObjectId _id, String ip, ArrayList<Prompt> prompts, boolean agent, String userEmail,
                    LocalDateTime createDate, LocalDateTime updateDate, boolean blocked, LocalDateTime blockedUntil,
                    int requestCount, LocalDateTime lastRequestDate
    ) {
        this._id = _id;
        this.ip = ip;
        this.prompts = prompts;
        this.agent = agent;
        this.userEmail = userEmail;
        this.createDate = createDate;
        this.updateDate = updateDate;

        this.blocked = blocked;
        this.blockedUntil = blockedUntil;
        this.requestCount = requestCount;
        this.lastRequestDate = lastRequestDate;
    }

    public IAPrompt(String ip, ArrayList<Prompt> prompts, boolean agent, String userEmail, LocalDateTime updateDate) {
        this.ip = ip;
        this.prompts = prompts;
        this.agent = agent;
        this.userEmail = userEmail;
        this.updateDate = updateDate;
    }

    public IAPrompt(String ip, ArrayList<Prompt> prompts, boolean agent, String userEmail,
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

    public ArrayList<Prompt> getPrompts() {
        return prompts;
    }

    public void setPrompts(ArrayList<Prompt> prompts) {
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

    public void setResponseInLastIndex(String response) {
        if(!prompts.isEmpty()) prompts.get(prompts.size() - 1).setResponse(response);
    }

    public Prompt getLastPrompt() {
        return prompts.isEmpty() ? null : prompts.get(prompts.size() - 1);
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(LocalDateTime blockedUntil) {
        this.blockedUntil = blockedUntil;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public LocalDateTime getLastRequestDate() {
        return lastRequestDate;
    }

    public void setLastRequestDate(LocalDateTime lastRequestDate) {
        this.lastRequestDate = lastRequestDate;
    }
}

