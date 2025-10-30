package co.edu.uco.ucochallenge.secondary.adapters.service;

import java.util.Collections;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import co.edu.uco.ucochallenge.crosscuting.exception.InfrastructureException;
import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.messages.MessageCodes;
import co.edu.uco.ucochallenge.crosscuting.messages.MessageServicePortHolder;
import co.edu.uco.ucochallenge.secondary.adapters.service.orchestration.CatalogService;
import co.edu.uco.ucochallenge.secondary.ports.service.MessageServicePort;

@Component
public class MessageServiceAdapter implements MessageServicePort {

    private final CatalogService catalogService;

    public MessageServiceAdapter(final CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostConstruct
    void configureHolder() {
        MessageServicePortHolder.configure(this);
    }

    @Override
    public String getMessage(final String key) {
        return getMessage(key, Collections.emptyMap());
    }

    @Override
    public String getMessage(final String key, final Map<String, String> parameters) {
        if (TextHelper.isEmpty(key)) {
            return TextHelper.getDefault();
        }

        final Map<String, String> safeParameters = ObjectHelper.getDefault(parameters, Collections.emptyMap());

        try {
            return catalogService.findMessageValue(key, safeParameters)
                    .filter(value -> !TextHelper.isEmpty(value))
                    .orElse(key);
        } catch (final WebClientResponseException.NotFound notFound) {
            return key;
        } catch (final WebClientRequestException | WebClientResponseException exception) {
            throw InfrastructureException.buildFromCatalog(
                    MessageCodes.Infrastructure.MessageService.UNAVAILABLE_TECHNICAL,
                    MessageCodes.Infrastructure.MessageService.UNAVAILABLE_USER,
                    Collections.emptyMap(), exception);
        }
    }
}
