package co.edu.uco.ucochallenge.secondary.adapters.service;

import java.util.Collections;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import co.edu.uco.ucochallenge.crosscuting.exception.ExceptionLayer;
import co.edu.uco.ucochallenge.crosscuting.exception.UcoChallengeException;
import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.messages.MessageKey;
import co.edu.uco.ucochallenge.crosscuting.messages.MessageServicePortHolder;
import co.edu.uco.ucochallenge.secondary.adapters.service.dto.RemoteCatalogEntry;
import co.edu.uco.ucochallenge.secondary.ports.service.MessageServicePort;

@Component
public class MessageServiceAdapter implements MessageServicePort {

    private final RestTemplate restTemplate;
    private final String endpoint;

    public MessageServiceAdapter(final RestTemplate restTemplate,
            @Value("${services.message.base-url}") final String endpoint) {
        this.restTemplate = restTemplate;
        this.endpoint = endpoint;
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
            final ResponseEntity<RemoteCatalogEntry> response = restTemplate.getForEntity(resolveUrl(key, safeParameters),
                    RemoteCatalogEntry.class);
            final RemoteCatalogEntry body = response.getBody();
            return body == null || TextHelper.isEmpty(body.value()) ? key : body.value();
        } catch (final HttpClientErrorException.NotFound notFound) {
            return key;
        } catch (final RestClientException exception) {
            throw UcoChallengeException.createTechnicalException(ExceptionLayer.APPLICATION,
                    MessageKey.GENERAL_TECHNICAL_ERROR, exception);
        }
    }

    private String resolveUrl(final String key, final Map<String, String> parameters) {
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(endpoint)
                .pathSegment(key);
        parameters.forEach(builder::queryParam);
        return builder.build(true).toUriString();
    }
}
