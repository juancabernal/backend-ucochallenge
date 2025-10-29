package co.edu.uco.ucochallenge.secondary.adapters.service;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Collections;

import co.edu.uco.ucochallenge.crosscuting.exception.InfrastructureException;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.messages.MessageCodes;
import co.edu.uco.ucochallenge.crosscuting.parameter.ParameterServicePortHolder;
import co.edu.uco.ucochallenge.secondary.adapters.service.dto.RemoteCatalogEntry;
import co.edu.uco.ucochallenge.secondary.ports.service.ParameterServicePort;

@Component
public class ParameterServiceAdapter implements ParameterServicePort {

    private final RestTemplate restTemplate;
    private final String endpoint;

    public ParameterServiceAdapter(final RestTemplate restTemplate,
            @Value("${services.parameters.base-url}") final String endpoint) {
        this.restTemplate = restTemplate;
        this.endpoint = endpoint;
    }

    @PostConstruct
    void configureHolder() {
        ParameterServicePortHolder.configure(this);
    }

    @Override
    public String getParameter(final String key) {
        if (TextHelper.isEmpty(key)) {
            return TextHelper.getDefault();
        }

        try {
            final ResponseEntity<RemoteCatalogEntry> response = restTemplate.getForEntity(resolveUrl(key),
                    RemoteCatalogEntry.class);
            final RemoteCatalogEntry body = response.getBody();
            return body == null || TextHelper.isEmpty(body.value()) ? TextHelper.getDefault() : body.value();
        } catch (final HttpClientErrorException.NotFound notFound) {
            return TextHelper.getDefault();
        } catch (final RestClientException exception) {
            throw InfrastructureException.buildFromCatalog(
                    MessageCodes.Infrastructure.ParameterService.UNAVAILABLE_TECHNICAL,
                    MessageCodes.Infrastructure.ParameterService.UNAVAILABLE_USER,
                    Collections.emptyMap(), exception);
        }
    }

    private String resolveUrl(final String key) {
        return UriComponentsBuilder.fromUriString(endpoint)
                .pathSegment(key)
                .build(true)
                .toUriString();
    }
}
