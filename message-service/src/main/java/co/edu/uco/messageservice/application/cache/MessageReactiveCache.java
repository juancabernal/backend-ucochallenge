package co.edu.uco.messageservice.application.cache;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import co.edu.uco.messageservice.application.mapper.MessageMapper;
import co.edu.uco.messageservice.domain.model.Message;
import co.edu.uco.messageservice.infrastructure.repository.MessageRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Dummy reactive cache that keeps a live snapshot of the message table. It is
 * initialised at startup and refreshed automatically either by service
 * operations or by a periodic polling mechanism that emulates an external
 * change detector.
 */
@Component
public class MessageReactiveCache {

    private static final Logger logger = LoggerFactory.getLogger(MessageReactiveCache.class);

    private final MessageRepository repository;
    private final MessageMapper mapper;
    private final ReactiveHotCache<String, Message> cache = new ReactiveHotCache<>();
    private final Duration pollingInterval;
    private Disposable pollingSubscription;

    public MessageReactiveCache(MessageRepository repository, MessageMapper mapper,
            @Value("${app.cache.polling-interval:PT5S}") Duration pollingInterval) {
        this.repository = repository;
        this.mapper = mapper;
        this.pollingInterval = pollingInterval;
    }

    @PostConstruct
    public void initialise() {
        logger.info("Initialising reactive message cache");
        refreshFromDatabase("startup").subscribe();
        startPolling();
    }

    @PreDestroy
    public void shutdown() {
        if (pollingSubscription != null) {
            pollingSubscription.dispose();
        }
    }

    public Mono<Void> refreshFromDatabase(String reason) {
        return repository.findAll()
                .map(mapper::toDomain)
                .collectList()
                .doOnNext(messages -> {
                    cache.replaceAll(messages, Message::code);
                    logger.debug("Message cache refreshed ({}), elements={}", reason, messages.size());
                })
                .then();
    }

    public Mono<Void> warmUp() {
        return refreshFromDatabase("manual");
    }

    public Flux<Message> snapshot() {
        return cache.snapshotFlux();
    }

    public Mono<Message> findByCode(String code) {
        return cache.get(code);
    }

    public Flux<List<Message>> changes() {
        return cache.changes();
    }

    public void register(Message message) {
        cache.put(message.code(), message);
    }

    public void remove(String code) {
        cache.remove(code);
    }

    private void startPolling() {
        pollingSubscription = Flux.interval(pollingInterval)
                .flatMap(tick -> refreshFromDatabase("polling"))
                .subscribe();
    }
}
