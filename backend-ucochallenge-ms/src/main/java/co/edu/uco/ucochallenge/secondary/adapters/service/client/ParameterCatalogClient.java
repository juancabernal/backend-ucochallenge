package co.edu.uco.ucochallenge.secondary.adapters.service.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.secondary.adapters.service.dto.RemoteCatalogEntry;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ParameterCatalogClient {

    private final WebClient webClient;

    public ParameterCatalogClient(@Qualifier("parameterCatalogWebClient") final WebClient webClient) {
        this.webClient = webClient;
    }

    public Optional<String> findValueByKey(final String key) {
        final RemoteCatalogEntry entry = webClient.get()
                .uri(builder -> builder.pathSegment(key).build())
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
}
