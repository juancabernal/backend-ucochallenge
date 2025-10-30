package co.edu.uco.messageservice.catalog;

import co.edu.uco.messageservice.model.Message;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * Catálogo en memoria responsable de mantener los mensajes actualizados y
 * publicar un flujo reactivo cada vez que ocurre un cambio. Todo se mantiene en
 * memoria sin depender de una base de datos externa.
 */
@Component
public class ReactiveMessageCatalog {

    private final ConcurrentMap<String, Message> store = new ConcurrentHashMap<>();
    private final Sinks.Many<CatalogEvent> eventSink = Sinks.many().replay().latest();

    public ReactiveMessageCatalog() {
        // Datos cargados estáticamente al iniciar la aplicación
        List<Message> initialMessages = List.of(
                new Message("welcome", "Bienvenido a la plataforma reactiva"),
                new Message("farewell", "Gracias por visitarnos"),
                new Message("reminder", "Recuerda practicar Project Reactor")
        );
        initialMessages.forEach(message -> store.put(message.key(), message));
        emitSnapshot();
    }

    /**
     * Devuelve todos los mensajes vigentes en forma reactiva.
     */
    public Flux<Message> findAll() {
        return Flux.defer(() -> Flux.fromIterable(store.values()));
    }

    /**
     * Devuelve un mensaje específico si existe.
     */
    public Mono<Message> findByKey(String key) {
        return Mono.defer(() -> Mono.justOrEmpty(store.get(key)));
    }

    /**
     * Inserta o actualiza un mensaje y notifica al resto de consumidores
     * reemitiento un nuevo snapshot del catálogo.
     */
    public Mono<Message> upsert(Message message) {
        return Mono.fromSupplier(() -> {
            store.put(message.key(), message);
            emitSnapshot();
            return message;
        });
    }

    /**
     * Elimina un mensaje y notifica a los suscriptores de la actualización.
     */
    public Mono<Boolean> delete(String key) {
        return Mono.fromSupplier(() -> {
            Message removed = store.remove(key);
            if (removed != null) {
                emitSnapshot();
                return true;
            }
            return false;
        });
    }

    /**
     * Exposición del flujo de eventos para otros componentes interesados.
     * Cada evento contiene un snapshot inmutable del catálogo completo y el
     * instante de generación.
     */
    public Flux<CatalogEvent> changes() {
        return eventSink.asFlux();
    }

    private void emitSnapshot() {
        Map<String, Message> snapshot = Map.copyOf(store);
        CatalogEvent event = new CatalogEvent(snapshot.values(), Instant.now());
        eventSink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    /**
     * Evento que encapsula la colección actual de mensajes.
     */
    public record CatalogEvent(Collection<Message> messages, Instant emittedAt) {
    }
}
