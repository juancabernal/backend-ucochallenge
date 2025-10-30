package co.edu.uco.ucochallenge.secondary.adapters.service.client;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;

import co.edu.uco.ucochallenge.crosscuting.helper.ObjectHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.secondary.adapters.service.dto.RemoteCatalogEntry;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MessageCatalogClient {

    private final WebClient webClient;

    public MessageCatalogClient(@Qualifier("messageCatalogWebClient") final WebClient webClient) {
        this.webClient = webClient;
    }

    public Optional<String> findValueByKey(final String key, final Map<String, String> parameters) {
        final Map<String, String> safeParameters = ObjectHelper.getDefault(parameters, Collections.emptyMap());

        final RemoteCatalogEntry entry = webClient.get()
                .uri(uriBuilder -> buildMessageUri(uriBuilder, key, safeParameters))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.empty();
                    }
                    if (response.statusCode().isError()) {
                        return response.createException().flatMap(Mono::error);
                    }
                    return response.bodyToMono(RemoteCatalogEntry.class);
                })
                .block();

        if (entry == null || TextHelper.isEmpty(entry.value())) {
            return Optional.empty();
        }

        return Optional.of(entry.value());
    }

    private java.net.URI buildMessageUri(final UriBuilder uriBuilder, final String key,
            final Map<String, String> parameters) {
        final UriBuilder builder = uriBuilder.pathSegment(key);
        parameters.forEach(builder::queryParam);
        return builder.build();
    }
}
