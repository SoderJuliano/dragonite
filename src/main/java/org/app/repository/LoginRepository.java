package org.app.repository;

import org.app.model.Login;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends MongoRepository<Login, String> {

    Optional<Login> findByUserIdAndPasswordAndEmail(String userId, String password, String email);
}
