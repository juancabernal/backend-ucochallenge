package co.edu.uco.parameterservice.catalog;

import co.edu.uco.parameterservice.model.Parameter;
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
 * Catálogo reactivo en memoria para parámetros dinámicos.
 */
@Component
public class ReactiveParameterCatalog {

    private final ConcurrentMap<String, Parameter> store = new ConcurrentHashMap<>();
    private final Sinks.Many<CatalogEvent> eventSink = Sinks.many().replay().latest();

    public ReactiveParameterCatalog() {
        List<Parameter> initialParameters = List.of(
                new Parameter("timeout", "5000"),
                new Parameter("feature.flag.beta", "false"),
                new Parameter("max.connections", "50")
        );
        initialParameters.forEach(parameter -> store.put(parameter.key(), parameter));
        emitSnapshot();
    }

    public Flux<Parameter> findAll() {
        return Flux.defer(() -> Flux.fromIterable(store.values()));
    }

    public Mono<Parameter> findByKey(String key) {
        return Mono.defer(() -> Mono.justOrEmpty(store.get(key)));
    }

    public Mono<Parameter> upsert(Parameter parameter) {
        return Mono.fromSupplier(() -> {
            store.put(parameter.key(), parameter);
            emitSnapshot();
            return parameter;
        });
    }

    public Mono<Boolean> delete(String key) {
        return Mono.fromSupplier(() -> {
            Parameter removed = store.remove(key);
            if (removed != null) {
                emitSnapshot();
                return true;
            }
            return false;
        });
    }

    public Flux<CatalogEvent> changes() {
        return eventSink.asFlux();
    }

    private void emitSnapshot() {
        Map<String, Parameter> snapshot = Map.copyOf(store);
        CatalogEvent event = new CatalogEvent(snapshot.values(), Instant.now());
        eventSink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public record CatalogEvent(Collection<Parameter> parameters, Instant emittedAt) {
    }
}
