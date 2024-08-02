package dev.hashnode.ishbhana.csfle;

import dev.hashnode.ishbhana.csfle.model.Person;
import dev.hashnode.ishbhana.csfle.mongo.MongoEncrypter;
import dev.hashnode.ishbhana.csfle.repository.PersonRepository;
import org.bson.BsonBinary;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ContextConfiguration(classes = {TestcontainersConfig.class, IntegrationTestConfig.class})
@Tag("integration")
@ActiveProfiles({"it"})
@SpringBootTest
public class MongoEncryptionIT {

    static final String SOCIAL_SECURITY_NUMBER_FIELD = "socialSecurityNumber";

    @Autowired
    PersonRepository personRepository;
    @SpyBean
    MongoEncrypter mongoEncrypter;
    @Captor
    ArgumentCaptor<Document> documentCaptor;

    @AfterEach
    void cleanUp() {
        personRepository.deleteAll();
    }

    @Test
    void shouldEncryptSocialSecurityNumberOnSave() {
        personRepository.save(Person.builder()
                .firstName("Joe")
                .lastName("Blogs")
                .socialSecurityNumber("123456")
                .build());

        verify(mongoEncrypter, times(1))
                .encrypt(
                        eq(SOCIAL_SECURITY_NUMBER_FIELD),
                        documentCaptor.capture());

        Document document = documentCaptor.getValue();
        assertThat(document.get(SOCIAL_SECURITY_NUMBER_FIELD))
                .isInstanceOf(BsonBinary.class);
    }

    @Test
    void shouldDecryptSocialSecurityNumberOnLoad() {
        Person persisted = personRepository.save(Person.builder()
                .firstName("Joe")
                .lastName("Blogs")
                .socialSecurityNumber("123456")
                .build());

        personRepository.findById(persisted.getId());

        verify(mongoEncrypter, times(1))
                .decrypt(
                        eq(SOCIAL_SECURITY_NUMBER_FIELD),
                        documentCaptor.capture());

        Document document = documentCaptor.getValue();
        assertThat(document.get(SOCIAL_SECURITY_NUMBER_FIELD))
                .isEqualTo("123456");

    }
}
