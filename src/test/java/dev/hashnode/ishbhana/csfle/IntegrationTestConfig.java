package dev.hashnode.ishbhana.csfle;

import com.mongodb.client.model.vault.DataKeyOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration
public class IntegrationTestConfig {

    @Bean
    @ConditionalOnProperty(
            name = "app.mongodb.encryption.provider", havingValue = "local")
    public DataKeyOptions dataKeyOptions() {
        DataKeyOptions options = new DataKeyOptions();
        options.keyAltNames(Collections.singletonList("test"));
        return options;
    }

    @Bean
    @ConditionalOnProperty(
            name = "app.mongodb.encryption.provider", havingValue = "local")
    public Map<String, Map<String, Object>> kmsProviderConfigMap() {
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", loadTestMasterKey());
        Map<String, Map<String, Object>> providersMap = new HashMap<>();
        providersMap.put("local", keyMap);
        return providersMap;
    }

    private byte[] loadTestMasterKey() {
        try {
            return readKey("src/test/resources/test-master.key");
        } catch (IOException e) {
            throw new IllegalStateException("Could not load local master key", e);
        }
    }

    public byte[] readKey(String masterKeyPath) throws IOException {
        byte[] masterKey = new byte[96];
        try (FileInputStream stream = new FileInputStream(masterKeyPath)) {
            if (stream.read(masterKey, 0, 96) == 0)
                throw new IOException("invalid key");
        }
        return masterKey;
    }
}
