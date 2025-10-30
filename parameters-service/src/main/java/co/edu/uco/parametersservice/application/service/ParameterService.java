package co.edu.uco.parametersservice.application.service;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private final Sinks.Many<ParameterChange> changeSink;

    public ParameterService(ParameterRepository repository, ParameterMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.changeSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public Flux<Parameter> findAll() {
        return Flux.defer(() -> repository.findAll().map(mapper::toDomain));
    }

    public Mono<Parameter> findByCode(String code) {
        return repository.findByCode(code)
                .map(mapper::toDomain)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Parameter with code %s was not found".formatted(code))));
    }

    public Mono<Parameter> createParameter(String code, String value) {
        Parameter parameter = mapper.withGeneratedId(mapper.from(code, value));
        ParameterEntity entity = mapper.toEntity(parameter);
        entity.setUpdatedAt(Instant.now());
        return repository.save(entity)
                .map(mapper::toDomain)
                .doOnNext(saved -> emitChange(ParameterChange.Type.CREATED, saved));
    }

    public Mono<Parameter> updateParameter(String code, String value) {
        return repository.findByCode(code)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Parameter with code %s was not found".formatted(code))))
                .flatMap(entity -> {
                    entity.setValue(value);
                    entity.setUpdatedAt(Instant.now());
                    return repository.save(entity);
                })
                .map(mapper::toDomain)
                .doOnNext(updated -> emitChange(ParameterChange.Type.UPDATED, updated));
    }

    public Mono<Parameter> deleteParameter(String code) {
        return repository.findByCode(code)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Parameter with code %s was not found".formatted(code))))
                .flatMap(entity -> repository.delete(entity).thenReturn(mapper.toDomain(entity)))
                .doOnNext(deleted -> emitChange(ParameterChange.Type.DELETED, deleted));
    }

    public Flux<ParameterChange> streamChanges() {
        return changeSink.asFlux();
    }

    private void emitChange(ParameterChange.Type type, Parameter payload) {
        changeSink.emitNext(new ParameterChange(type, payload), Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
