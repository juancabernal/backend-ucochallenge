package co.edu.uco.messageservice.application.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * In-memory reactive cache that keeps the latest snapshot of a dataset and
 * exposes it as a hot flux so that observers can be notified immediately after
 * a change is produced programmatically or by a scheduled refresh.
 */
public class ReactiveHotCache<K, V> {

    private final AtomicReference<Map<K, V>> state = new AtomicReference<>(Map.of());
    private final Sinks.Many<List<V>> sink = Sinks.many().replay().latest();

    public ReactiveHotCache() {
        sink.emitNext(List.of(), Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public void replaceAll(Collection<V> values, Function<V, K> keyExtractor) {
        Map<K, V> nextState = values.stream()
                .collect(Collectors.toMap(keyExtractor, Function.identity(), (left, right) -> right, LinkedHashMap::new));
        updateState(nextState);
    }

    public void put(K key, V value) {
        state.updateAndGet(current -> {
            Map<K, V> copy = new LinkedHashMap<>(current);
            copy.put(key, value);
            return Collections.unmodifiableMap(copy);
        });
        emitSnapshot();
    }

    public void remove(K key) {
        state.updateAndGet(current -> {
            if (!current.containsKey(key)) {
                return current;
            }
            Map<K, V> copy = new LinkedHashMap<>(current);
            copy.remove(key);
            return Collections.unmodifiableMap(copy);
        });
        emitSnapshot();
    }

    public List<V> snapshot() {
        return List.copyOf(state.get().values());
    }

    public Flux<V> snapshotFlux() {
        return Flux.defer(() -> Flux.fromIterable(snapshot()));
    }

    public Mono<V> get(K key) {
        return Mono.defer(() -> Mono.justOrEmpty(state.get().get(key)));
    }

    public Flux<List<V>> changes() {
        return sink.asFlux();
    }

    private void updateState(Map<K, V> nextState) {
        Map<K, V> candidate = Collections.unmodifiableMap(new LinkedHashMap<>(nextState));
        Map<K, V> previous = state.getAndSet(candidate);
        if (!Objects.equals(previous, candidate)) {
            emitSnapshot();
        }
    }

    private void emitSnapshot() {
        sink.emitNext(List.copyOf(state.get().values()), Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
