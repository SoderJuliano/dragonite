package org.app.repository;

import org.app.model.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Payment findByPaymentId(String paymentId);
    List<Payment> findByUserId(String userId);
}
