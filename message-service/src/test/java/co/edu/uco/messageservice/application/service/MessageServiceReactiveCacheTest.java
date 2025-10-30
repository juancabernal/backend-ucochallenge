package co.edu.uco.messageservice.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import co.edu.uco.messageservice.application.cache.MessageReactiveCache;
import co.edu.uco.messageservice.application.mapper.MessageMapper;
import co.edu.uco.messageservice.domain.model.Message;
import co.edu.uco.messageservice.infrastructure.persistence.entity.MessageEntity;
import co.edu.uco.messageservice.infrastructure.repository.MessageRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class MessageServiceReactiveCacheTest {

    @Mock
    private MessageRepository repository;

    private MessageService messageService;
    private MessageReactiveCache cache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MessageMapper mapper = new MessageMapper();
        cache = new MessageReactiveCache(repository, mapper, Duration.ofHours(1));
        when(repository.findAll()).thenReturn(Flux.empty());
        cache.warmUp().block();
        messageService = new MessageService(repository, mapper, cache);
    }

    @Test
    void whenMessageIsCreatedThenNextReadReflectsCacheImmediately() {
        MessageEntity entity = new MessageEntity();
        entity.setId(java.util.UUID.randomUUID());
        entity.setCode("WELCOME");
        entity.setText("Welcome!");
        entity.setLanguage("en");
        entity.setUpdatedAt(Instant.now());

        when(repository.save(any(MessageEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(messageService.createMessage("WELCOME", "Welcome!", "en"))
                .expectNextMatches(message -> message.code().equals("WELCOME")
                        && message.text().equals("Welcome!")
                        && message.language().equals("en"))
                .verifyComplete();

        StepVerifier.create(messageService.findAll().collectList())
                .expectNextMatches(list -> list.size() == 1 && list.getFirst().code().equals("WELCOME"))
                .verifyComplete();
    }

    @Test
    void whenMessageIsUpdatedCacheEmitsNewSnapshot() {
        MessageEntity existing = new MessageEntity();
        existing.setId(java.util.UUID.randomUUID());
        existing.setCode("GREETING");
        existing.setText("Hi");
        existing.setLanguage("en");
        existing.setUpdatedAt(Instant.now());

        when(repository.findByCode("GREETING")).thenReturn(Mono.just(existing));
        when(repository.save(any(MessageEntity.class))).thenAnswer(invocation -> {
            MessageEntity updated = invocation.getArgument(0);
            return Mono.just(updated);
        });

        cache.register(new Message(existing.getId(), existing.getCode(), existing.getText(), existing.getLanguage(),
                existing.getUpdatedAt()));

        StepVerifier.create(messageService.updateMessage("GREETING", "Hola", "es"))
                .expectNextMatches(message -> message.text().equals("Hola") && message.language().equals("es"))
                .verifyComplete();

        StepVerifier.create(messageService.streamCacheSnapshots().next())
                .expectNextMatches(snapshot -> snapshot.stream().anyMatch(message ->
                        message.code().equals("GREETING") && message.text().equals("Hola")
                                && message.language().equals("es")))
                .verifyComplete();
    }
}
