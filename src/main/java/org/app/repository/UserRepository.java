package org.app.repository;

import org.app.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    @Query("{ 'name' : ?0, 'contact.email' : { $elemMatch: { $in: ?1 } } }")
    Optional<User> findByNameAndAnyEmail(String name, List<String> emails);

    @Query("{ 'contact.email' : ?0 }")
    Optional<User> findFirstByEmail(String email);

    boolean existsById(String id);

    boolean existsByActivationCodeAndId(String activationCode, String email);
}
