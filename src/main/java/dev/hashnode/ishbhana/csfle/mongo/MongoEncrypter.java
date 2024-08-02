package dev.hashnode.ishbhana.csfle.mongo;

import com.mongodb.client.model.vault.EncryptOptions;
import com.mongodb.client.vault.ClientEncryption;
import lombok.AllArgsConstructor;
import org.bson.BsonBinary;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.EncryptionAlgorithms;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class MongoEncrypter {

    private static final String ENCRYPTION_ALGORITHM =
            EncryptionAlgorithms.AEAD_AES_256_CBC_HMAC_SHA_512_Random;

    private final ClientEncryption mongoClientEncryption;
    private final BsonBinary mongoClientEncryptionDataKey;

    public void encrypt(String key, Document document) {
        Optional.ofNullable(document.get(key))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(BsonString::new)
                .ifPresent(bson -> document.replace(key, encrypt(bson)));
    }

    public void decrypt(String key, Document document) {
        Optional.ofNullable(document.get(key))
                .filter(Binary.class::isInstance)
                .map(Binary.class::cast)
                .map(this::decrypt)
                .ifPresent(bson -> document.replace(key, bson.asString().getValue()));
    }

    private BsonBinary encrypt(BsonValue bsonValue) {
        return mongoClientEncryption.encrypt(bsonValue,
                new EncryptOptions(ENCRYPTION_ALGORITHM)
                        .keyId(mongoClientEncryptionDataKey));
    }

    private BsonValue decrypt(Binary input) {
        return mongoClientEncryption.decrypt(
                new BsonBinary(input.getType(), input.getData()));
    }
}
