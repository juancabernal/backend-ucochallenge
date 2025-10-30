package co.edu.uco.parametersservice.application.service;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.uco.parametersservice.application.cache.ParameterReactiveCache;
import co.edu.uco.parametersservice.application.mapper.ParameterMapper;
import co.edu.uco.parametersservice.domain.event.ParameterChange;
import co.edu.uco.parametersservice.domain.model.Parameter;
import co.edu.uco.parametersservice.infrastructure.persistence.entity.ParameterEntity;
import co.edu.uco.parametersservice.infrastructure.repository.ParameterRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class ParameterService {

    private final ParameterRepository repository;
    private final ParameterMapper mapper;
    private final ParameterReactiveCache cache;
    private final Sinks.Many<ParameterChange> changeSink;

    public ParameterService(ParameterRepository repository, ParameterMapper mapper, ParameterReactiveCache cache) {
        this.repository = repository;
        this.mapper = mapper;
        this.cache = cache;
        this.changeSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public Flux<Parameter> findAll() {
        return cache.snapshot();
    }

    public Mono<Parameter> findByKey(String key) {
        return cache.findByKey(key)
                .switchIfEmpty(repository.findByKey(key)
                        .map(mapper::toDomain)
                        .doOnNext(cache::register))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Parameter with key %s was not found".formatted(key))));
    }

    public Mono<Parameter> createParameter(String key, String value) {
        Parameter parameter = mapper.withGeneratedId(mapper.from(key, value));
        ParameterEntity entity = mapper.toEntity(parameter);
        entity.setUpdatedAt(Instant.now());
        return repository.save(entity)
                .map(mapper::toDomain)
                .doOnNext(saved -> {
                    cache.register(saved);
                    emitChange(ParameterChange.Type.CREATED, saved);
                });
    }

    public Mono<Parameter> updateParameter(String key, String value) {
        return repository.findByKey(key)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Parameter with key %s was not found".formatted(key))))
                .flatMap(entity -> {
                    entity.setValue(value);
                    entity.setUpdatedAt(Instant.now());
                    return repository.save(entity);
                })
                .map(mapper::toDomain)
                .doOnNext(updated -> {
                    cache.register(updated);
                    emitChange(ParameterChange.Type.UPDATED, updated);
                });
    }

    public Mono<Parameter> deleteParameter(String key) {
        return repository.findByKey(key)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Parameter with key %s was not found".formatted(key))))
                .flatMap(entity -> repository.delete(entity).thenReturn(mapper.toDomain(entity)))
                .doOnNext(deleted -> {
                    cache.remove(key);
                    emitChange(ParameterChange.Type.DELETED, deleted);
                });
    }

    public Flux<ParameterChange> streamChanges() {
        return changeSink.asFlux();
    }

    private void emitChange(ParameterChange.Type type, Parameter payload) {
        changeSink.emitNext(new ParameterChange(type, payload), Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public Flux<List<Parameter>> streamCacheSnapshots() {
        return cache.changes();
    }
}
