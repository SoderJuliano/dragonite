package org.app.model;

import jakarta.validation.constraints.NotNull;

public class Prompt {

    @NotNull
    private String request;
    private String response;

    public Prompt(){
        // Default contructor
    }

    public Prompt(String request, String response) {
        this.request = request;
        this.response = response;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
