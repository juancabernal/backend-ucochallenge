package co.edu.uco.parametersservice.catalog;

import co.edu.uco.parametersservice.model.Parameter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación sencilla del catálogo reactivo de parámetros. Mantiene los
 * datos en memoria y emite un evento cada vez que cambia algo para que otros
 * componentes puedan reaccionar inmediatamente si lo desean.
 */
public class ReactiveParameterCatalog {

    private final ConcurrentHashMap<String, Parameter> storage = new ConcurrentHashMap<>();
    private final Sinks.Many<CatalogEvent<Parameter>> updates = Sinks.many().replay().latest();

    public ReactiveParameterCatalog() {
        preload();
        storage.values().forEach(parameter ->
            updates.tryEmitNext(new CatalogEvent<>(CatalogEventType.CREATED, parameter))
        );
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
        return Mono.fromCallable(() -> {
            Parameter previous = storage.put(parameter.key(), parameter);
            CatalogEventType type = previous == null ? CatalogEventType.CREATED : CatalogEventType.UPDATED;
            updates.tryEmitNext(new CatalogEvent<>(type, parameter));
            return parameter;
        });
    }

    public Mono<Void> delete(String key) {
        return Mono.fromRunnable(() -> {
            Parameter removed = storage.remove(key);
            if (removed != null) {
                updates.tryEmitNext(new CatalogEvent<>(CatalogEventType.DELETED, removed));
            }
        }).then();
    }

    public Flux<CatalogEvent<Parameter>> events() {
        return updates.asFlux();
    }

    public record CatalogEvent<T>(CatalogEventType type, T payload) {
    }

    public enum CatalogEventType {
        CREATED,
        UPDATED,
        DELETED
    }
}
