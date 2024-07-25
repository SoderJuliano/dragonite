package org.app.services.impl;

import org.app.services.TwoStepService;
import org.app.utils.LocalLog;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;

import static org.app.utils.Commons.notEmpty;
import static org.app.utils.LocalLog.log;

@Service
public class TwoStepServiceImpl implements TwoStepService {
    private HttpClient client = HttpClient.newHttpClient();
    private final String abraHost = "https://abra-api.top";

    @Override
    public boolean sendMessage(String email, String message, String subject, String key) {
        String requestBody = String.format("""
                {
                  "title": "%s",
                  "key": "%s",
                  "content": "%s"
                }""", subject, key, message);

        try {
            HttpResponse<String> response = client.send(mountAbraRequest(requestBody),
                    HttpResponse.BodyHandlers.ofString());
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

    @Override
    public boolean sendEmail(String email, String message, String subject) throws UnsupportedEncodingException {

        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String encodedSubject = URLEncoder.encode("[en]Your confirmation token/[pt]Código de confirmação", StandardCharsets.UTF_8);
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

        String url = String.format("%s/email/send-email/%s/%s/%s", abraHost, encodedEmail, encodedMessage, encodedSubject);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "*/*")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log(":send_email Email response status code: " + response.statusCode() + " for email: " + email);

            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (IOException | InterruptedException e) {
            String err = notEmpty(e.getMessage()) ? e.getMessage() : "";
            log(":bug Exception during sending email to: "+email+". Exception: "+err);
            e.printStackTrace();
            return false;
        }
    }


    private HttpRequest mountAbraRequest(String requestBody) {
        return HttpRequest.newBuilder()
                .uri(URI.create(abraHost+ "/notifications"))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }
}
