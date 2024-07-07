package org.app.repository;

import org.app.model.Login;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginRepository extends MongoRepository<Login, String> {

    List<Login> findByUserIdAndPasswordAndEmail(String userId, String password, String email);

    List<Login> findByEmailAndPassword(String email, String password);
}
