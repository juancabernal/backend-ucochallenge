package co.edu.uco.messageservice.service;

import co.edu.uco.messageservice.catalog.ReactiveMessageCatalog;
import co.edu.uco.messageservice.model.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio reactivo que orquesta las operaciones sobre el catálogo en memoria.
 */
@Service
public class ReactiveMessageService {

    private final ReactiveMessageCatalog catalog;

    public ReactiveMessageService(ReactiveMessageCatalog catalog) {
        this.catalog = catalog;
    }

    public Flux<Message> findAll() {
        return catalog.findAll();
    }

    public Mono<Message> findByKey(String key) {
        return catalog.findByKey(key);
    }

    public Mono<Message> upsert(String key, String value) {
        return catalog.upsert(new Message(key, value));
    }

    public Mono<Boolean> delete(String key) {
        return catalog.delete(key);
    }

    /**
     * Devuelve un flujo infinito que emite un snapshot completo cada vez que el
     * catálogo cambia. Puede utilizarse para pruebas o SSE.
     */
    public Flux<Message> watchCatalog() {
        return catalog.changes().flatMapIterable(ReactiveMessageCatalog.CatalogEvent::messages);
    }
}
