package org.app.services;

public interface TwoStepService {

   public boolean sendMessage(String email, String message, String subject, String key);

   public boolean validateEmail(String token);
}
