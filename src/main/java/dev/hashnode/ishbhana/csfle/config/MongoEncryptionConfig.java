package dev.hashnode.ishbhana.csfle.config;

import com.mongodb.ClientEncryptionSettings;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import lombok.AllArgsConstructor;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
@Configuration
@AutoConfigureAfter(MongoAutoConfiguration.class)
@EnableConfigurationProperties(MongoEncryptionProperties.class)
public class MongoEncryptionConfig {

    private final MongoEncryptionProperties properties;

    @Bean
    public ClientEncryption mongoClientEncryption(
            ObjectProvider<MongoClientSettingsBuilderCustomizer> customizers,
            MongoClientSettings standardSettings,
            Map<String, Map<String, Object>> kmsProviderConfigMap) {

        MongoClientSettings.Builder builder =
                MongoClientSettings.builder(standardSettings);

        customizers.orderedStream()
                .toList()
                .forEach(c -> c.customize(builder));

        MongoClientSettings customSettings = builder.build();

        ClientEncryptionSettings clientEncryptionSettings =
                ClientEncryptionSettings.builder()
                        .keyVaultMongoClientSettings(customSettings)
                        .keyVaultNamespace(properties.getKeyVaultNamespace())
                        .kmsProviders(kmsProviderConfigMap)
                        .build();

        return ClientEncryptions.create(clientEncryptionSettings);
    }

    @Bean
    public BsonBinary mongoClientEncryptionDataKey(
            ClientEncryption clientEncryption, DataKeyOptions dataKeyOptions) {
        BsonDocument key = clientEncryption.getKeyByAltName(
                properties.getKeyVaultAlias());
        if (key == null) {
            return clientEncryption.createDataKey(
                    properties.getProvider(), dataKeyOptions);
        } else {
            return key.getBinary("_id");
        }
    }

    @Bean
    @ConditionalOnProperty(
            name = "app.mongodb.encryption.provider", havingValue = "aws")
    public DataKeyOptions dataKeyOptions() {
        DataKeyOptions options = new DataKeyOptions();
        BsonDocument masterKey = new BsonDocument();
        masterKey.put("provider", new BsonString("aws"));
        masterKey.put("region", new BsonString(properties.getAwsRegion()));
        masterKey.put("key", new BsonString(properties.getAwsKmsKeyId()));
        options.masterKey(masterKey);
        options.keyAltNames(Collections.singletonList(properties.getKeyVaultAlias()));
        return options;
    }

    @Bean
    @ConditionalOnProperty(
            name = "app.mongodb.encryption.provider", havingValue = "aws")
    public Map<String, Map<String, Object>> kmsProviderConfigMap() {
        return Map.of("aws", Map.of(
                "accessKeyId", properties.getAwsAccessKeyId(),
                "secretAccessKey", properties.getAwsSecretAccessKey()
        ));
    }
}
