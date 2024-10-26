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

    boolean existsByActivationCodeAndId(String activationCode, String email);

    boolean existsByContactEmailAndLanguage(String email, String language);

}
