package org.app.services;

import java.io.UnsupportedEncodingException;

public interface TwoStepService {

   public boolean sendMessage(String email, String message, String subject, String key);

   public boolean validateEmail(String token);

   boolean sendEmail(String email, String message, String subject) throws UnsupportedEncodingException;
}
