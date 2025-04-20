package org.app.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Document(collection = "payments")
@Schema(description = "Representa um pagamento realizado.")
public class Payment {
    @Id
    @Schema(description = "ID gerado pelo MongoDB", hidden = true)
    private String id;           // ID gerado pelo MongoDB
    @Schema(description = "ID do pagamento no PayPal", example = "PAYID-1234567890ABCDEF")
    private String paymentId;    // ID do PayPal (ex.: "PAYID-123456")
    @Schema(description = "ID do usuário no sistema", example = "user_12345")
    private String userId;       // ID do usuário no seu sistema
    @Schema(description = "E-mail do cliente", example = "cliente@exemplo.com")
    private String email;        // Email do cliente
    @Schema(description = "Valor do pagamento em USD", example = "50.75")
    private double amount;       // Valor em USD
    @Schema(description = "Status do pagamento", allowableValues = {"PENDENTE", "CONCLUIDO", "ESTORNADO"}, example = "PENDENTE")
    private String status;      // "PENDENTE", "CONCLUIDO", "ESTORNADO"
    @Schema(description = "Data de criação do pagamento", example = "2025-04-19T12:30:02.112Z")
    private LocalDateTime createdAt;

    public Payment(String id, String paymentId, String userId, String email, double amount, String status, LocalDateTime createdAt) {
        this.id = id;
        this.paymentId = paymentId;
        this.userId = userId;
        this.email = email;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Payment() {
//        default contructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
