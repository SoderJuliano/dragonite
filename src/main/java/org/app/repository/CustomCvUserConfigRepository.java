package org.app.repository;

import org.app.model.PageUserConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomCvUserConfigRepository extends MongoRepository<PageUserConfig, String>  {
}
