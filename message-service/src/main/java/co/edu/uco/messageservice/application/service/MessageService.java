package co.edu.uco.messageservice.application.service;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.uco.messageservice.application.mapper.MessageMapper;
import co.edu.uco.messageservice.domain.event.MessageChange;
import co.edu.uco.messageservice.domain.model.Message;
import co.edu.uco.messageservice.infrastructure.persistence.entity.MessageEntity;
import co.edu.uco.messageservice.infrastructure.repository.MessageRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class MessageService {

    private final MessageRepository repository;
    private final MessageMapper mapper;
    private final Sinks.Many<MessageChange> changeSink;

    public MessageService(MessageRepository repository, MessageMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.changeSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public Flux<Message> findAll() {
        return Flux.defer(() -> repository.findAll().map(mapper::toDomain));
    }

    public Mono<Message> findByCode(String code) {
        return repository.findByCode(code)
                .map(mapper::toDomain)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Message with code %s was not found".formatted(code))));
    }

    public Mono<Message> createMessage(String code, String value) {
        Message message = mapper.withGeneratedId(mapper.from(code, value));
        MessageEntity entity = mapper.toEntity(message);
        entity.setUpdatedAt(Instant.now());
        return repository.save(entity)
                .map(mapper::toDomain)
                .doOnNext(saved -> emitChange(MessageChange.Type.CREATED, saved));
    }

    public Mono<Message> updateMessage(String code, String value) {
        return repository.findByCode(code)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Message with code %s was not found".formatted(code))))
                .flatMap(entity -> {
                    entity.setValue(value);
                    entity.setUpdatedAt(Instant.now());
                    return repository.save(entity);
                })
                .map(mapper::toDomain)
                .doOnNext(updated -> emitChange(MessageChange.Type.UPDATED, updated));
    }

    public Mono<Message> deleteMessage(String code) {
        return repository.findByCode(code)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Message with code %s was not found".formatted(code))))
                .flatMap(entity -> repository.delete(entity).thenReturn(mapper.toDomain(entity)))
                .doOnNext(deleted -> emitChange(MessageChange.Type.DELETED, deleted));
    }

    public Flux<MessageChange> streamChanges() {
        return changeSink.asFlux();
    }

    private void emitChange(MessageChange.Type type, Message payload) {
        changeSink.emitNext(new MessageChange(type, payload), Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
