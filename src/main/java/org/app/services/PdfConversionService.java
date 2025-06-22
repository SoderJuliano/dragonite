package org.app.services;

import org.app.model.requests.ConversionRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PdfConversionService {

    private static final String NODE_SERVICE_URL = "http://localhost:3200/convert";

    private final RestTemplate restTemplate;

    public PdfConversionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public byte[] convertToPdf(ConversionRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ConversionRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<byte[]> response = restTemplate.postForEntity(
                NODE_SERVICE_URL,
                entity,
                byte[].class
        );

        return response.getBody();
    }
}