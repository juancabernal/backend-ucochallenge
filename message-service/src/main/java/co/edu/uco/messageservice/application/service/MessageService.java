package co.edu.uco.messageservice.application.service;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.uco.messageservice.application.cache.MessageReactiveCache;
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
    private final MessageReactiveCache cache;
    private final Sinks.Many<MessageChange> changeSink;

    public MessageService(MessageRepository repository, MessageMapper mapper, MessageReactiveCache cache) {
        this.repository = repository;
        this.mapper = mapper;
        this.cache = cache;
        this.changeSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public Flux<Message> findAll() {
        return cache.snapshot();
    }

    public Mono<Message> findByCode(String code) {
        return cache.findByCode(code)
                .switchIfEmpty(repository.findByCode(code)
                        .map(mapper::toDomain)
                        .doOnNext(cache::register))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Message with code %s was not found".formatted(code))));
    }

    public Mono<Message> createMessage(String code, String text, String language) {
        Message message = mapper.withGeneratedId(mapper.from(code, text, language));
        MessageEntity entity = mapper.toEntity(message);
        entity.setUpdatedAt(Instant.now());
        return repository.save(entity)
                .map(mapper::toDomain)
                .doOnNext(saved -> {
                    cache.register(saved);
                    emitChange(MessageChange.Type.CREATED, saved);
                });
    }

    public Mono<Message> updateMessage(String code, String text, String language) {
        return repository.findByCode(code)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Message with code %s was not found".formatted(code))))
                .flatMap(entity -> {
                    entity.setText(text);
                    entity.setLanguage(language);
                    entity.setUpdatedAt(Instant.now());
                    return repository.save(entity);
                })
                .map(mapper::toDomain)
                .doOnNext(updated -> {
                    cache.register(updated);
                    emitChange(MessageChange.Type.UPDATED, updated);
                });
    }

    public Mono<Message> deleteMessage(String code) {
        return repository.findByCode(code)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Message with code %s was not found".formatted(code))))
                .flatMap(entity -> repository.delete(entity).thenReturn(mapper.toDomain(entity)))
                .doOnNext(deleted -> {
                    cache.remove(code);
                    emitChange(MessageChange.Type.DELETED, deleted);
                });
    }

    public Flux<MessageChange> streamChanges() {
        return changeSink.asFlux();
    }

    public Flux<List<Message>> streamCacheSnapshots() {
        return cache.changes();
    }

    private void emitChange(MessageChange.Type type, Message payload) {
        changeSink.emitNext(new MessageChange(type, payload), Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
