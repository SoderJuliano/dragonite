package org.app;

import org.app.config.SecretManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
  public static void main(String[] args) {
    SecretManager.setMongoURI();
    SpringApplication.run(Main.class, args);
  }
}
