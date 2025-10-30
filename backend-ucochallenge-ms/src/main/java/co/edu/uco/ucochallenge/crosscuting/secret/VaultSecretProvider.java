package co.edu.uco.ucochallenge.crosscuting.secret;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueResponseSupport;
import org.springframework.vault.core.VaultTemplate;

/**
 * HashiCorp Vault backed implementation of {@link SecretProvider}.
 */
@Component
public class VaultSecretProvider implements SecretProvider {

    private static final Logger logger = LoggerFactory.getLogger(VaultSecretProvider.class);

    private final VaultKeyValueOperations operations;
    private final String context;
    private final Environment environment;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public VaultSecretProvider(final VaultTemplate vaultTemplate,
            final Environment environment,
            @Value("${spring.cloud.vault.kv.backend}") final String backend,
            @Value("${spring.cloud.vault.kv.default-context}") final String context) {
        this.operations = vaultTemplate.opsForKeyValue(backend, KeyValueBackend.KV_2);
        this.context = context;
        this.environment = environment;
    }

    @Override
    public String get(final String key) {
        return cache.computeIfAbsent(key, this::resolveSecret);
    }

    private String resolveSecret(final String key) {
        final Optional<String> vaultValue = readFromVault(key);
        if (vaultValue.isPresent()) {
            return vaultValue.get();
        }

        final String fallback = environment.getProperty(key);
        if (fallback != null) {
            logger.warn("Secret '{}' not found in Vault context '{}', using fallback from environment", key, context);
            return fallback;
        }

        throw new IllegalStateException(String.format("Secret '%s' not found in Vault context '%s'", key, context));
    }

    private Optional<String> readFromVault(final String key) {
        try {
            final KeyValueResponseSupport<Map<String, Object>> response = operations.get(context);
            final Map<String, Object> data = response == null ? Collections.emptyMap() : response.getData();
            if (data == null || !data.containsKey(key)) {
                return Optional.empty();
            }

            final Object value = data.get(key);
            return Optional.ofNullable(value == null ? null : String.valueOf(value));
        } catch (final VaultException exception) {
            logger.error("Unable to read secrets from Vault context '{}': {}", context, exception.getMessage());
            return Optional.empty();
        }
    }
}
