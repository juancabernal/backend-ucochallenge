package co.edu.uco.ucochallenge.crosscuting.integration.message;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import co.edu.uco.ucochallenge.crosscuting.exception.InfrastructureException;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.integration.message.dto.MessageResponse;
import co.edu.uco.ucochallenge.crosscuting.integration.message.property.ServiceEndpointsProperties;
import co.edu.uco.ucochallenge.crosscuting.messages.MessageCodes;

public class MessageServiceRestClient implements MessageCatalog {

        private final RestTemplate restTemplate;
        private final ServiceEndpointsProperties.MessageServiceProperties properties;

        public MessageServiceRestClient(final RestTemplate restTemplate,
                        final ServiceEndpointsProperties.MessageServiceProperties properties) {
                this.restTemplate = restTemplate;
                this.properties = properties;
        }

        @Override
        public String getMessage(final String code, final Map<String, String> parameters) {
                try {
                        final UriComponentsBuilder builder = UriComponentsBuilder
                                        .fromHttpUrl(properties.resolveMessageUrl(code));
                        if (parameters != null && !parameters.isEmpty()) {
                                final LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
                                parameters.forEach(queryParams::add);
                                builder.queryParams(queryParams);
                        }
                        builder.queryParam("_", Instant.now().toEpochMilli());
                        final ResponseEntity<MessageResponse> response = restTemplate.getForEntity(builder.build(true).toUri(),
                                        MessageResponse.class);
                        final MessageResponse body = response.getBody();
                        if (body == null || TextHelper.isEmpty(body.message())) {
                                return TextHelper.getDefault(code);
                        }
                        return body.message();
                } catch (final RestClientException exception) {
                        throw InfrastructureException.buildFromCatalog(MessageCodes.Infrastructure.MESSAGE_SERVICE_UNAVAILABLE,
                                        MessageCodes.Infrastructure.MESSAGE_SERVICE_UNAVAILABLE_USER,
                                        Map.of("service", "message-service"), exception);
                }
        }
}
