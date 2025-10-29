package co.edu.uco.ucochallenge.crosscuting.integration.parameter;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import co.edu.uco.ucochallenge.crosscuting.exception.InfrastructureException;
import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.integration.message.property.ServiceEndpointsProperties.ServiceEndpointProperties;
import co.edu.uco.ucochallenge.crosscuting.integration.parameter.dto.ParameterResponse;
import co.edu.uco.ucochallenge.crosscuting.messages.MessageCodes;

public class ParameterServiceRestClient implements ParameterCatalog {

    private final RestTemplate restTemplate;
    private final ServiceEndpointProperties properties;

    public ParameterServiceRestClient(final RestTemplate restTemplate, final ServiceEndpointProperties properties) {
        this.restTemplate = ObjectHelper.getDefault(restTemplate, new RestTemplate());
        this.properties = ObjectHelper.getDefault(properties, new ServiceEndpointProperties());
    }

    @Override
    public String getValue(final String key) {
        final String normalizedKey = TextHelper.getDefault(key);

        try {
            final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                    .pathSegment("api", "v1", "parameters", normalizedKey);
            final ParameterResponse response = restTemplate.getForObject(builder.build(true).toUri(), ParameterResponse.class);
            if (response == null || TextHelper.isEmpty(response.value())) {
                throw buildInvalidResponseException(null);
            }
            return TextHelper.getDefault(response.value());
        } catch (final HttpClientErrorException exception) {
            throw buildUnavailableException(exception);
        } catch (final RestClientException exception) {
            throw buildUnavailableException(exception);
        }
    }

    private InfrastructureException buildUnavailableException(final Exception cause) {
        return InfrastructureException.buildFromCatalog(
                MessageCodes.Infrastructure.ParameterService.UNAVAILABLE_TECHNICAL,
                MessageCodes.Infrastructure.ParameterService.UNAVAILABLE_USER, cause);
    }

    private InfrastructureException buildInvalidResponseException(final Exception cause) {
        return InfrastructureException.buildFromCatalog(
                MessageCodes.Infrastructure.ParameterService.INVALID_RESPONSE_TECHNICAL,
                MessageCodes.Infrastructure.ParameterService.INVALID_RESPONSE_USER, cause);
    }
}
