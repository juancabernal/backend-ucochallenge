package co.edu.uco.ucochallenge.crosscuting.secret;

/**
 * Abstraction used to retrieve secret values from the configured secret
 * manager.
 */
public interface SecretProvider {

    /**
     * Obtains the value associated with the provided key.
     *
     * @param key identifier of the secret within the configured secret context
     * @return secret value as string
     * @throws IllegalStateException when the value cannot be resolved
     */
    String get(String key);
}