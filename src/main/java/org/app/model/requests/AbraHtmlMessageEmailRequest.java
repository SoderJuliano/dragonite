package org.app.model.requests;

public record AbraHtmlMessageEmailRequest (String email, String subject, String htmlContent) {}
