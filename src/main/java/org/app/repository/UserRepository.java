package org.app.repository;

import org.app.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<Object> findByName(String name);

    @Query("{ 'name' : ?0, 'contact.email' : { $in: ?1 } }")
    Optional<User> findByNameAndAnyEmail(String name, List<String> emails);
}
