package co.edu.uco.parametersservice.service;

import co.edu.uco.parametersservice.catalog.ReactiveParameterCatalog;
import co.edu.uco.parametersservice.model.Parameter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Mono<Parameter> save(Parameter parameter) {
        return catalog.save(parameter);
    }

    public Mono<Void> delete(String key) {
        return catalog.delete(key);
    }

    public Flux<ReactiveParameterCatalog.CatalogEvent<Parameter>> events() {
        return catalog.events();
    }
}
