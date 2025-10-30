package co.edu.uco.ucochallenge.domain.secret.port;

/**
 * Domain port used to obtain sensitive configuration values.
 */
public interface SecretProviderPort {

    /**
     * Retrieves the value of a secret by its unique name.
     *
     * @param name identifier of the secret to fetch.
     * @return resolved secret value.
     * @throws IllegalStateException when the secret cannot be resolved.
     */
    String getSecret(String name);
}
