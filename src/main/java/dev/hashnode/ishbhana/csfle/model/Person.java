package dev.hashnode.ishbhana.csfle.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Value
@Immutable
@Document(collection = "person")
public class Person {
    @Id
    String id;
    String firstName;
    String lastName;
    String socialSecurityNumber;
}
