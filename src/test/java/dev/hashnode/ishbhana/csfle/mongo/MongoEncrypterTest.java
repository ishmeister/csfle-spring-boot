package dev.hashnode.ishbhana.csfle.mongo;

import com.mongodb.client.model.vault.EncryptOptions;
import com.mongodb.client.vault.ClientEncryption;
import org.bson.BsonBinary;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.Binary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoEncrypterTest {

    static final String PLAINTEXT = "plaintext";
    static final String ENCRYPTED = "encrypted";
    static final String FIELD_NAME = "field-name";

    @Mock
    ClientEncryption clientEncryption;
    @InjectMocks
    MongoEncrypter testSubject;

    @Test
    void encrypt_encryptsFieldIfExists() {
        BsonBinary encryptedBson = new BsonBinary(ENCRYPTED.getBytes());

        when(clientEncryption.encrypt(
                eq(new BsonString(PLAINTEXT)),
                any(EncryptOptions.class))).thenReturn(encryptedBson);

        Document document = new Document();
        document.append(FIELD_NAME, PLAINTEXT);

        testSubject.encrypt(FIELD_NAME, document);

        assertThat(document.get(FIELD_NAME)).isEqualTo(encryptedBson);
    }

    @Test
    void encrypt_doesNothingIfFieldDoesNotExist() {
        testSubject.encrypt(FIELD_NAME, new Document());
        verify(clientEncryption, never()).encrypt(any(), any());
    }

    @Test
    void encrypt_throwsExceptionIfFieldExistsButIsNotAStringField() {
        Document document = new Document();
        document.append(FIELD_NAME, Boolean.TRUE);

        assertThatThrownBy(() -> testSubject.encrypt(FIELD_NAME, document))
                .isInstanceOf(ClassCastException.class);

        verify(clientEncryption, never()).encrypt(any(), any());
    }

    @Test
    void decrypt_decryptsFieldIfExists() {
        Binary encryptedBson = new Binary(ENCRYPTED.getBytes());

        when(clientEncryption.decrypt(any(BsonBinary.class))).thenReturn(new BsonString(PLAINTEXT));

        Document document = new Document();
        document.append(FIELD_NAME, encryptedBson);

        testSubject.decrypt(FIELD_NAME, document);

        assertThat(document.get(FIELD_NAME)).isEqualTo(PLAINTEXT);
    }

    @Test
    void decrypt_doesNothingIfFieldDoesNotExist() {
        testSubject.decrypt(FIELD_NAME, new Document());
        verify(clientEncryption, never()).decrypt(any());
    }
}