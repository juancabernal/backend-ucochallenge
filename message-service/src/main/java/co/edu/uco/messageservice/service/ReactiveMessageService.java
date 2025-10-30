package co.edu.uco.messageservice.service;

import co.edu.uco.messageservice.catalog.ReactiveMessageCatalog;
import co.edu.uco.messageservice.model.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Application service that orchestrates read/write operations on the reactive
 * catalog.
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

    public Mono<Message> save(Message message) {
        return catalog.save(message);
    }

    public Mono<Void> delete(String key) {
        return catalog.delete(key);
    }

    public Flux<ReactiveMessageCatalog.CatalogEvent<Message>> events() {
        return catalog.events();
    }
}
