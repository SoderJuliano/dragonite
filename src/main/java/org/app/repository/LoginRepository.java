package org.app.repository;

import org.app.model.Login;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoginRepository extends MongoRepository<Login, String> {

    List<Login> findByUserIdAndEmail(String userId, String email);

    List<Login> findByEmailAndLanguage(String email, String language);

    List<Login> findByEmail(List<String> email);

    void deleteByUserId(String id);

    Optional<Login> findByUserId(String userId);
}
