package co.edu.uco.ucochallenge.crosscuting.integration.message;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import co.edu.uco.ucochallenge.crosscuting.exception.InfrastructureException;
import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.integration.message.dto.MessageResponse;
import co.edu.uco.ucochallenge.crosscuting.integration.message.property.ServiceEndpointsProperties.ServiceEndpointProperties;
import co.edu.uco.ucochallenge.crosscuting.messages.MessageCodes;

public class MessageServiceRestClient implements MessageCatalog {

    private final RestTemplate restTemplate;
    private final ServiceEndpointProperties properties;

    public MessageServiceRestClient(final RestTemplate restTemplate, final ServiceEndpointProperties properties) {
        this.restTemplate = ObjectHelper.getDefault(restTemplate, new RestTemplate());
        this.properties = ObjectHelper.getDefault(properties, new ServiceEndpointProperties());
    }

    @Override
    public String getMessage(final String key) {
        return getMessage(key, Collections.emptyMap());
    }

    @Override
    public String getMessage(final String key, final Map<String, String> parameters) {
        final String normalizedKey = TextHelper.getDefault(key);
        final Map<String, String> normalizedParameters = ObjectHelper.getDefault(parameters, Collections.emptyMap());

        try {
            final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .pathSegment("api", "v1", "messages", normalizedKey);
            normalizedParameters.forEach(builder::queryParam);

            final MessageResponse response = restTemplate.getForObject(builder.build(true).toUri(), MessageResponse.class);
            if (response == null || TextHelper.isEmpty(response.value())) {
                return normalizedKey;
            }
            return TextHelper.getDefault(response.value());
        } catch (final HttpClientErrorException exception) {
            if (HttpStatus.NOT_FOUND.equals(exception.getStatusCode())) {
                return normalizedKey;
            }
            throw InfrastructureException.buildFromCatalog(
                    MessageCodes.Infrastructure.MessageService.UNAVAILABLE_TECHNICAL,
                    MessageCodes.Infrastructure.MessageService.UNAVAILABLE_USER, exception);
        } catch (final RestClientException exception) {
            throw InfrastructureException.buildFromCatalog(
                    MessageCodes.Infrastructure.MessageService.UNAVAILABLE_TECHNICAL,
                    MessageCodes.Infrastructure.MessageService.UNAVAILABLE_USER, exception);
        }
    }
}
