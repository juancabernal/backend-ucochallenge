package co.edu.uco.parametersservice.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import co.edu.uco.parametersservice.application.cache.ParameterReactiveCache;
import co.edu.uco.parametersservice.application.mapper.ParameterMapper;
import co.edu.uco.parametersservice.domain.model.Parameter;
import co.edu.uco.parametersservice.infrastructure.persistence.entity.ParameterEntity;
import co.edu.uco.parametersservice.infrastructure.repository.ParameterRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ParameterServiceReactiveCacheTest {

    @Mock
    private ParameterRepository repository;

    private ParameterService parameterService;
    private ParameterReactiveCache cache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ParameterMapper mapper = new ParameterMapper();
        cache = new ParameterReactiveCache(repository, mapper, Duration.ofHours(1));
        when(repository.findAll()).thenReturn(Flux.empty());
        cache.warmUp().block();
        parameterService = new ParameterService(repository, mapper, cache);
    }

    @Test
    void whenParameterIsCreatedNextReadReturnsLatestValue() {
        ParameterEntity entity = new ParameterEntity();
        entity.setId(java.util.UUID.randomUUID());
        entity.setKey("max.connections");
        entity.setValue("10");
        entity.setUpdatedAt(Instant.now());

        when(repository.save(any(ParameterEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(parameterService.createParameter("max.connections", "10"))
                .expectNextMatches(parameter -> parameter.key().equals("max.connections")
                        && parameter.value().equals("10"))
                .verifyComplete();

        StepVerifier.create(parameterService.findAll().collectList())
                .expectNextMatches(list -> list.size() == 1 && list.getFirst().value().equals("10"))
                .verifyComplete();
    }

    @Test
    void whenParameterIsUpdatedCachePublishesNewSnapshot() {
        ParameterEntity existing = new ParameterEntity();
        existing.setId(java.util.UUID.randomUUID());
        existing.setKey("feature.enabled");
        existing.setValue("false");
        existing.setUpdatedAt(Instant.now());

        when(repository.findByKey("feature.enabled")).thenReturn(Mono.just(existing));
        when(repository.save(any(ParameterEntity.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        cache.register(new Parameter(existing.getId(), existing.getKey(), existing.getValue(), existing.getUpdatedAt()));

        StepVerifier.create(parameterService.updateParameter("feature.enabled", "true"))
                .expectNextMatches(parameter -> parameter.value().equals("true"))
                .verifyComplete();

        StepVerifier.create(parameterService.streamCacheSnapshots().next())
                .expectNextMatches(snapshot -> snapshot.stream().anyMatch(parameter ->
                        parameter.key().equals("feature.enabled") && parameter.value().equals("true")))
                .verifyComplete();
    }
}
