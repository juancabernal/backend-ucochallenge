package co.edu.uco.parametersservice.catalog;

import co.edu.uco.parametersservice.model.Parameter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local reactive catalog that keeps configuration parameters and notifies
 * subscribers of any change.
 */
public class ReactiveParameterCatalog {

    private final ConcurrentHashMap<String, Parameter> storage = new ConcurrentHashMap<>();
    private final Sinks.Many<CatalogEvent<Parameter>> sink = Sinks.many().multicast().directAllOrNothing();
    private final Flux<CatalogEvent<Parameter>> changeStream = sink.asFlux();

    public ReactiveParameterCatalog() {
        preload();
    }

    private void preload() {
        List<Parameter> defaults = List.of(
            new Parameter("feature.toggle.realtime", "true"),
            new Parameter("security.max-sessions", "3"),
            new Parameter("ui.theme", "dark")
        );
        defaults.forEach(parameter -> storage.put(parameter.key(), parameter));
    }

    public Mono<Parameter> findByKey(String key) {
        return Mono.defer(() -> Mono.justOrEmpty(storage.get(key)));
    }

    public Flux<Parameter> findAll() {
        return Flux.defer(() -> Flux.fromIterable(storage.values()));
    }

    public Mono<Parameter> save(Parameter parameter) {
        return Mono.fromSupplier(() -> {
            Parameter previous = storage.put(parameter.key(), parameter);
            CatalogEvent.CatalogEventType type = previous == null
                ? CatalogEvent.CatalogEventType.CREATED
                : CatalogEvent.CatalogEventType.UPDATED;
            sink.tryEmitNext(new CatalogEvent<>(type, parameter));
            return parameter;
        });
    }

    public Mono<Void> delete(String key) {
        return Mono.fromRunnable(() -> {
            Parameter removed = storage.remove(key);
            if (removed != null) {
                sink.tryEmitNext(new CatalogEvent<>(CatalogEvent.CatalogEventType.DELETED, removed));
            }
        });
    }

    public Flux<CatalogEvent<Parameter>> changes() {
        return changeStream;
    }

    public Flux<Parameter> liveView() {
        return Flux.defer(() -> Flux.fromIterable(storage.entrySet()))
            .map(Map.Entry::getValue)
            .concatWith(changes().map(CatalogEvent::payload));
    }
}
