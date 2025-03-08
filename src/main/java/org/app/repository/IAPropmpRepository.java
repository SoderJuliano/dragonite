package org.app.repository;

import org.app.model.IAPrompt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAPropmpRepository extends MongoRepository<IAPrompt, String> {

    Optional<IAPrompt> findByIp(String ip);
}
