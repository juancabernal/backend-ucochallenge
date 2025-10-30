package co.edu.uco.ucochallenge.crosscuting.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * Ensures a Vault token is available before the Vault auto-configuration kicks in.
 * <p>
 * Spring Cloud Vault requires the {@code spring.cloud.vault.token} property (or a
 * {@code ~/.vault-token} file) to be present when using TOKEN authentication. When
 * the application is launched from an IDE, that property might not be set early
 * enough for the auto-configuration to detect it. This {@link EnvironmentPostProcessor}
 * inspects a few well-known locations and exposes the token property before the
 * application context is created.
 */
public class VaultTokenEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(VaultTokenEnvironmentPostProcessor.class);

    private static final String PROPERTY_SOURCE_NAME = "vaultTokenPropertySource";
    private static final String PROPERTY_NAME = "spring.cloud.vault.token";

    @Override
    public void postProcessEnvironment(final ConfigurableEnvironment environment, final SpringApplication application) {
        String token = environment.getProperty(PROPERTY_NAME);

        if (!StringUtils.hasText(token)) {
            token = resolveFromEnvironmentVariables(environment);
        }

        if (!StringUtils.hasText(token)) {
            token = resolveFromVaultTokenFile();
        }

        if (StringUtils.hasText(token)) {
            Map<String, Object> properties = new HashMap<>();
            properties.put(PROPERTY_NAME, token);
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
            logger.debug("Vault token successfully resolved and registered in the environment");
        } else {
            logger.warn(() -> String.format("No Vault token configured. Provide the '%s' property, set the "
                    + "'VAULT_TOKEN' or 'SPRING_CLOUD_VAULT_TOKEN' environment variables, or create a ~/.vault-token file.",
                    PROPERTY_NAME));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private String resolveFromEnvironmentVariables(final ConfigurableEnvironment environment) {
        String token = environment.getProperty("VAULT_TOKEN");
        if (StringUtils.hasText(token)) {
            logger.debug("Vault token resolved from VAULT_TOKEN environment variable");
            return token;
        }

        token = environment.getProperty("SPRING_CLOUD_VAULT_TOKEN");
        if (StringUtils.hasText(token)) {
            logger.debug("Vault token resolved from SPRING_CLOUD_VAULT_TOKEN environment variable");
            return token;
        }

        return null;
    }

    private String resolveFromVaultTokenFile() {
        String userHome = System.getProperty("user.home");
        if (!StringUtils.hasText(userHome)) {
            return null;
        }

        Path tokenFile = Path.of(userHome, ".vault-token");
        if (!Files.isReadable(tokenFile)) {
            return null;
        }

        try {
            String token = Files.readString(tokenFile).trim();
            if (StringUtils.hasText(token)) {
                logger.debug("Vault token resolved from ~/.vault-token file");
                return token;
            }
        } catch (IOException ex) {
            logger.warn("Unable to read Vault token file '{}': {}", tokenFile, ex.getMessage());
        }

        return null;
    }
}
