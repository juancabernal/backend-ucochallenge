package co.edu.uco.parameterservice.service;

import co.edu.uco.parameterservice.catalog.ReactiveParameterCatalog;
import co.edu.uco.parameterservice.model.Parameter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio que administra parámetros apoyándose en el catálogo reactivo.
 */
@Service
public class ReactiveParameterService {

    private final ReactiveParameterCatalog catalog;

    public ReactiveParameterService(ReactiveParameterCatalog catalog) {
        this.catalog = catalog;
    }

    public Flux<Parameter> findAll() {
        return catalog.findAll();
    }

    public Mono<Parameter> findByKey(String key) {
        return catalog.findByKey(key);
    }

    public Mono<Parameter> upsert(String key, String value) {
        return catalog.upsert(new Parameter(key, value));
    }

    public Mono<Boolean> delete(String key) {
        return catalog.delete(key);
    }

    public Flux<Parameter> watchCatalog() {
        return catalog.changes().flatMapIterable(ReactiveParameterCatalog.CatalogEvent::parameters);
    }
}
