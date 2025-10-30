package co.edu.uco.ucochallenge.crosscuting.secret;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
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
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

/**
 * HashiCorp Vault-backed implementation of {@link SecretProvider}.
 */
@Component
public class VaultSecretProvider implements SecretProvider {

    private static final Logger logger = LoggerFactory.getLogger(VaultSecretProvider.class);

    private final VaultKeyValueOperations operations;
    private final String context;
    private final Environment environment;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public VaultSecretProvider(
            final VaultTemplate vaultTemplate,
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
        return readFromVault(key)
                .or(() -> Optional.ofNullable(environment.getProperty(key)))
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Secret '%s' not found in Vault context '%s'", key, context)));
    }

    private Optional<String> readFromVault(final String key) {
        try {
            VaultResponseSupport<Map<String, Object>> response = operations.get(context);
            Map<String, Object> data = response != null && response.getData() != null
                    ? response.getData()
                    : Collections.emptyMap();

            Object value = data.get(key);
            return Optional.ofNullable(Objects.toString(value, null));

        } catch (VaultException e) {
            logger.error("Unable to read secrets from Vault context '{}': {}", context, e.getMessage());
            return Optional.empty();
        }
    }
}
