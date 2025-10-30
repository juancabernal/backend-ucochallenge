package co.edu.uco.ucochallenge.infrastructure.secondary.secret;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.domain.secret.port.SecretProviderPort;

/**
 * Secondary adapter that retrieves secrets from Azure Key Vault.
 */
@Component
@Primary
@ConditionalOnProperty(name = "azure.keyvault.url")
public class AzureKeyVaultSecretAdapter implements SecretProviderPort {

    private static final Logger log = LoggerFactory.getLogger(AzureKeyVaultSecretAdapter.class);

    private final SecretClient client;

    public AzureKeyVaultSecretAdapter(@Value("${azure.keyvault.url}") final String vaultUrl) {
        this.client = new SecretClientBuilder()
                .vaultUrl(vaultUrl)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
        log.info("Azure Key Vault initialized at {}", vaultUrl);
    }

    @Override
    public String getSecret(final String name) {
        try {
            String value = client.getSecret(name).getValue();
            log.info("Secret '{}' successfully retrieved from Azure Key Vault", name);
            return value;
        } catch (Exception e) {
            log.error("Failed to retrieve secret '{}' from Azure Key Vault: {}", name, e.getMessage());
            throw new IllegalStateException("Error retrieving secret from Azure Key Vault", e);
        }
    }
}
