package co.edu.uco.messageservice.catalog;

import co.edu.uco.messageservice.model.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory reactive catalog that stores {@link Message} instances and emits
 * change notifications for any consumer interested in being synchronized with
 * the latest data.
 */
public class ReactiveMessageCatalog {

    private final ConcurrentHashMap<String, Message> storage = new ConcurrentHashMap<>();
    private final Sinks.Many<CatalogEvent<Message>> sink = Sinks.many().multicast().directAllOrNothing();
    private final Flux<CatalogEvent<Message>> changeStream = sink.asFlux();

    public ReactiveMessageCatalog() {
        preload();
    }

    private void preload() {
        List<Message> defaults = List.of(
            new Message("welcome", "¡Bienvenido a la plataforma reactiva!"),
            new Message("bye", "Gracias por visitarnos, vuelve pronto"),
            new Message("support", "Escríbenos a soporte@uco.edu.co")
        );
        defaults.forEach(message -> storage.put(message.key(), message));
    }

    /**
     * Retrieve a message by its key. Emits empty when the key does not exist.
     */
    public Mono<Message> findByKey(String key) {
        return Mono.defer(() -> Mono.justOrEmpty(storage.get(key)));
    }

    /**
     * Retrieve all messages reflecting the most up-to-date snapshot.
     */
    public Flux<Message> findAll() {
        return Flux.defer(() -> Flux.fromIterable(storage.values()));
    }

    /**
     * Save or update a message. The returned {@link Mono} completes with the
     * latest saved value and triggers a change event.
     */
    public Mono<Message> save(Message message) {
        return Mono.fromSupplier(() -> {
            Message previous = storage.put(message.key(), message);
            CatalogEvent.CatalogEventType type = previous == null
                ? CatalogEvent.CatalogEventType.CREATED
                : CatalogEvent.CatalogEventType.UPDATED;
            sink.tryEmitNext(new CatalogEvent<>(type, message));
            return message;
        });
    }

    /**
     * Delete a message if present and notify subscribers.
     */
    public Mono<Void> delete(String key) {
        return Mono.fromRunnable(() -> {
            Message removed = storage.remove(key);
            if (removed != null) {
                sink.tryEmitNext(new CatalogEvent<>(CatalogEvent.CatalogEventType.DELETED, removed));
            }
        });
    }

    /**
     * A hot stream that emits every catalog change in real time.
     */
    public Flux<CatalogEvent<Message>> changes() {
        return changeStream;
    }

    /**
     * Provides a convenient view that emits the current snapshot and then any
     * subsequent changes as soon as they occur.
     */
    public Flux<Message> liveView() {
        return Flux.defer(() -> Flux.fromIterable(storage.entrySet()))
            .map(Map.Entry::getValue)
            .concatWith(changes().map(CatalogEvent::payload));
    }
}
