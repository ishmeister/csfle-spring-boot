package dev.hashnode.ishbhana.csfle.repository;

import dev.hashnode.ishbhana.csfle.model.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {
}
