package dev.hashnode.ishbhana.csfle.mongo;

import dev.hashnode.ishbhana.csfle.model.Person;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class PersonMongoEventListener
        extends AbstractMongoEventListener<Person> {

    private static final String
            SOCIAL_SECURITY_NUMBER = "socialSecurityNumber";

    private final MongoEncrypter encrypter;

    @Override
    public void onBeforeSave(BeforeSaveEvent<Person> event) {
        Optional.ofNullable(event.getDocument())
                .ifPresent(doc ->
                        encrypter.encrypt(SOCIAL_SECURITY_NUMBER, doc));
    }

    @Override
    public void onAfterLoad(AfterLoadEvent<Person> event) {
        Optional.ofNullable(event.getDocument())
                .ifPresent(doc ->
                        encrypter.decrypt(SOCIAL_SECURITY_NUMBER, doc));
    }
}
