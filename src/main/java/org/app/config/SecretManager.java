package org.app.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SecretManager {
    private static final Properties properties = new Properties();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static JsonNode geminiSecrets;


    static {
//        String path = "/home/soder/Área de trabalho/app/secrets.txt";
//        String path = "/home/soder/Desktop/app/secrets.txt";
        String path = "/home/julianos/Documentos/workdir/app/secrets.txt";

        try (FileInputStream input = new FileInputStream(path)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load secrets file", e);
        }

        try (FileReader reader = new FileReader("/home/julianos/Documentos/workdir/gemini_api_key.json")) {
            geminiSecrets = objectMapper.readTree(reader);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load gemini secrets file", e);
        }
    }

    public static String getSecret(String key) {
        return properties.getProperty(key);
    }

    public static String getGeminiSecret(String key) {
        return geminiSecrets.get(key).asText();
    }

    public static void setMongoURI() {
        String mongoURI = getSecret("MONGODB_URI");
        System.setProperty("mongodb.uri", mongoURI);
    }
}
