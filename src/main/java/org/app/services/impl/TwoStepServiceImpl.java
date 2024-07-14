package org.app.services.impl;

import org.app.services.TwoStepService;
import org.app.utils.LocalLog;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.app.utils.LocalLog.log;

@Service
public class TwoStepServiceImpl implements TwoStepService {
    HttpClient client = HttpClient.newHttpClient();

    @Override
    public boolean sendMessage(String email, String message, String subject, String key) {
        String requestBody = String.format("""
                {
                  "title": "%s",
                  "key": "%s",
                  "content": "%s"
                }""", subject, key, message);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://abra-api.top/notifications"))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log(":email Abra response status code: " + response.statusCode() + " for email: " + email + " with key: " + key);
            log("email Response body: " + response.body());
        } catch (IOException | InterruptedException e) {
            LocalLog.logErr(":bug Failed to send email to: " + email + " with key: " + key + " due to: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean validateEmail(String token) {
        return false;
    }
}
