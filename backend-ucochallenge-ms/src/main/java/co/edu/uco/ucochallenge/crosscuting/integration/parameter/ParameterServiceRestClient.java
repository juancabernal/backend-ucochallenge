package co.edu.uco.ucochallenge.crosscuting.integration.parameter;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import co.edu.uco.ucochallenge.crosscuting.exception.InfrastructureException;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.integration.message.property.ServiceEndpointsProperties;
import co.edu.uco.ucochallenge.crosscuting.integration.parameter.dto.ParameterResponse;
import co.edu.uco.ucochallenge.crosscuting.messages.MessageCodes;

public class ParameterServiceRestClient implements ParameterCatalog {

        private final RestTemplate restTemplate;
        private final ServiceEndpointsProperties.ParameterServiceProperties properties;

        public ParameterServiceRestClient(final RestTemplate restTemplate,
                        final ServiceEndpointsProperties.ParameterServiceProperties properties) {
                this.restTemplate = restTemplate;
                this.properties = properties;
        }

        @Override
        public String getValue(final String code) {
                try {
                        final ResponseEntity<ParameterResponse> response = restTemplate.getForEntity(
                                        UriComponentsBuilder.fromHttpUrl(properties.resolveParameterUrl(code))
                                                        .queryParam("_", Instant.now().toEpochMilli())
                                                        .build(true).toUri(),
                                        ParameterResponse.class);
                        final ParameterResponse body = response.getBody();
                        if (body == null || TextHelper.isEmpty(body.value())) {
                                return TextHelper.getDefault(code);
                        }
                        return body.value();
                } catch (final RestClientException exception) {
                        throw InfrastructureException.buildFromCatalog(
                                        MessageCodes.Infrastructure.PARAMETER_SERVICE_UNAVAILABLE,
                                        MessageCodes.Infrastructure.PARAMETER_SERVICE_UNAVAILABLE_USER,
                                        Map.of("service", "parameter-service"), exception);
                }
        }
}
