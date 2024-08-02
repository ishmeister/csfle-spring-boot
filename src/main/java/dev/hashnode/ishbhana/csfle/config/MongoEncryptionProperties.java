package dev.hashnode.ishbhana.csfle.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "app.mongodb.encryption")
public class MongoEncryptionProperties {
    private String provider;
    private String keyVaultAlias;
    private String keyVaultNamespace;
    private String awsRegion;
    private String awsKmsKeyId;
    private String awsAccessKeyId;
    private String awsSecretAccessKey;
}
