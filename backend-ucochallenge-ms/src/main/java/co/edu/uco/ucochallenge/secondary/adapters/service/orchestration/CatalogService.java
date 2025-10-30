package co.edu.uco.ucochallenge.secondary.adapters.service.orchestration;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.secondary.adapters.service.client.MessageCatalogClient;
import co.edu.uco.ucochallenge.secondary.adapters.service.client.ParameterCatalogClient;

@Service
public class CatalogService {

    private final MessageCatalogClient messageCatalogClient;
    private final ParameterCatalogClient parameterCatalogClient;

    public CatalogService(final MessageCatalogClient messageCatalogClient,
            final ParameterCatalogClient parameterCatalogClient) {
        this.messageCatalogClient = messageCatalogClient;
        this.parameterCatalogClient = parameterCatalogClient;
    }

    public Optional<String> findMessageValue(final String key, final Map<String, String> parameters) {
        return messageCatalogClient.findValueByKey(key, parameters);
    }

    public Optional<String> findParameterValue(final String key) {
        return parameterCatalogClient.findValueByKey(key);
    }
}
