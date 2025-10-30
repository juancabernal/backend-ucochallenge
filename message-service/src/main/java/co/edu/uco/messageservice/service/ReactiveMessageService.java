package co.edu.uco.messageservice.service;

import co.edu.uco.messageservice.catalog.CatalogEvent;
import co.edu.uco.messageservice.catalog.ReactiveMessageCatalog;
import co.edu.uco.messageservice.model.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Application service that orchestrates read/write operations on the reactive
 * catalog. It subscribes to the change stream so the catalog remains consistent
 * for every HTTP request.
 */
@Service
public class ReactiveMessageService {

    private final ReactiveMessageCatalog catalog;
    private final Flux<Message> liveCatalog;

    public ReactiveMessageService(ReactiveMessageCatalog catalog) {
        this.catalog = catalog;
        // share() so every subscriber observes the same stream without
        // re-subscribing to the underlying sink.
        this.liveCatalog = catalog.liveView().share();
    }

    public Flux<Message> streamAll() {
        return liveCatalog;
    }

    public Flux<Message> findAll() {
        return catalog.findAll();
    }

    public Mono<Message> findByKey(String key) {
        return catalog.findByKey(key);
    }

    public Mono<Message> save(Message message) {
        return catalog.save(message);
    }

    public Mono<Void> delete(String key) {
        return catalog.delete(key);
    }

    public Flux<CatalogEvent<Message>> changes() {
        return catalog.changes();
    }
}
