package org.app.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Document(collection = "payments")
public class Payment {
    @Id
    private String id;           // ID gerado pelo MongoDB
    private String paymentId;    // ID do PayPal (ex.: "PAYID-123456")
    private String userId;       // ID do usuário no seu sistema
    private String email;        // Email do cliente
    private double amount;       // Valor em USD
    private String status;      // "PENDENTE", "CONCLUIDO", "ESTORNADO"
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
