package org.app.repository;

import org.app.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    @Query("{ 'contact.email' : { $elemMatch: { $in: ?0 } }, 'language' : ?1 }")
    Optional<User> findByAnyEmailAndLanguage(List<String> emails, String language);

    @Query("{ 'contact.email' : ?0, 'language' : ?1 }")
    Optional<User> findFirstByEmailAndLanguage(String email, String language);

    boolean existsById(String id);

    boolean existsByActivationCodeAndId(String activationCode, String id);

    boolean existsByContactEmailAndLanguage(String email, String language);

    //Aqui pra saber se tem conta premium tanto faz a conta entao da pra fazer uma busaca simples
    //{ "contact": { "email": "user@example.com" }, "premium": true, "language": "pt-br" }
    //{ "contact": { "email": "user@example.com" }, "premium": true, "language": "en-us" }
    @Query("{ 'contact.email' : ?0, 'premium' : true }")
    Boolean hasPremiumAccount(String email);

    List<User> findAllByContactEmail(String email);
}

