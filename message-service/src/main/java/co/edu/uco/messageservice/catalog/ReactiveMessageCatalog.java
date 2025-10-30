package co.edu.uco.messageservice.catalog;

import co.edu.uco.messageservice.model.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Catálogo reactivo en memoria. Usa una {@link ConcurrentHashMap} como
 * almacenamiento principal y un {@link Sinks.Many} para notificar cambios.
 *
 * La idea es que cada operación escriba en memoria y publique un evento. Los
 * siguientes GET leen del mapa (ya actualizado) y, si alguien necesita
 * reaccionar en caliente, puede suscribirse al flujo de eventos.
 */
public class ReactiveMessageCatalog {

    private final ConcurrentHashMap<String, Message> storage = new ConcurrentHashMap<>();
    private final Sinks.Many<CatalogEvent<Message>> updates = Sinks.many().replay().latest();

    public ReactiveMessageCatalog() {
        preload();
        // Emitimos el estado inicial para nuevos suscriptores.
        storage.values().forEach(message ->
            updates.tryEmitNext(new CatalogEvent<>(CatalogEventType.CREATED, message))
        );
    }

    private void preload() {
        List<Message> defaults = List.of(
            new Message("welcome", "¡Bienvenido a la plataforma reactiva!"),
            new Message("bye", "Gracias por visitarnos, vuelve pronto"),
            new Message("support", "Escríbenos a soporte@uco.edu.co")
        );
        defaults.forEach(message -> storage.put(message.key(), message));
    }

    public Mono<Message> findByKey(String key) {
        return Mono.defer(() -> Mono.justOrEmpty(storage.get(key)));
    }

    public Flux<Message> findAll() {
        return Flux.defer(() -> Flux.fromIterable(storage.values()));
    }

    public Mono<Message> save(Message message) {
        return Mono.fromCallable(() -> {
            Message previous = storage.put(message.key(), message);
            CatalogEventType type = previous == null ? CatalogEventType.CREATED : CatalogEventType.UPDATED;
            updates.tryEmitNext(new CatalogEvent<>(type, message));
            return message;
        });
    }

    public Mono<Void> delete(String key) {
        return Mono.fromRunnable(() -> {
            Message removed = storage.remove(key);
            if (removed != null) {
                updates.tryEmitNext(new CatalogEvent<>(CatalogEventType.DELETED, removed));
            }
        }).then();
    }

    public Flux<CatalogEvent<Message>> events() {
        return updates.asFlux();
    }

    /** Evento simple que indica el tipo de cambio y la carga asociada. */
    public record CatalogEvent<T>(CatalogEventType type, T payload) {
    }

    public enum CatalogEventType {
        CREATED,
        UPDATED,
        DELETED
    }
}
