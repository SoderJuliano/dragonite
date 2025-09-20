package org.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SecretManager {
  private static final Properties properties = new Properties();
  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
//    String path = "/home/soder/Área de Trabalho/app/secrets.txt";
    // String path = "/home/soder/Desktop/app/secrets.txt";
     String path = "/home/julianos/Documentos/workdir/app/secrets.txt";

    try (FileInputStream input = new FileInputStream(path)) {
      properties.load(input);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load secrets file", e);
    }
  }

  public static String getSecret(String key) {
    return properties.getProperty(key);
  }

  public static void setMongoURI() {
    String mongoURI = getSecret("MONGODB_URI");
    System.setProperty("mongodb.uri", mongoURI);
  }
}
