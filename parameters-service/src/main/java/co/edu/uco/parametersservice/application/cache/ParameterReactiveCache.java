package co.edu.uco.parametersservice.application.cache;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import co.edu.uco.parametersservice.application.mapper.ParameterMapper;
import co.edu.uco.parametersservice.domain.model.Parameter;
import co.edu.uco.parametersservice.infrastructure.repository.ParameterRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ParameterReactiveCache {

    private static final Logger logger = LoggerFactory.getLogger(ParameterReactiveCache.class);

    private final ParameterRepository repository;
    private final ParameterMapper mapper;
    private final ReactiveHotCache<String, Parameter> cache = new ReactiveHotCache<>();
    private final Duration pollingInterval;
    private Disposable pollingSubscription;

    public ParameterReactiveCache(ParameterRepository repository, ParameterMapper mapper,
            @Value("${app.cache.polling-interval:PT5S}") Duration pollingInterval) {
        this.repository = repository;
        this.mapper = mapper;
        this.pollingInterval = pollingInterval;
    }

    @PostConstruct
    public void initialise() {
        logger.info("Initialising reactive parameter cache");
        refreshFromDatabase("startup").subscribe();
        startPolling();
    }

    @PreDestroy
    public void shutdown() {
        if (pollingSubscription != null) {
            pollingSubscription.dispose();
        }
    }

    public Mono<Void> refreshFromDatabase(String reason) {
        return repository.findAll()
                .map(mapper::toDomain)
                .collectList()
                .doOnNext(parameters -> {
                    cache.replaceAll(parameters, Parameter::key);
                    logger.debug("Parameter cache refreshed ({}), elements={}", reason, parameters.size());
                })
                .then();
    }

    public Mono<Void> warmUp() {
        return refreshFromDatabase("manual");
    }

    public Flux<Parameter> snapshot() {
        return cache.snapshotFlux();
    }

    public Mono<Parameter> findByKey(String key) {
        return cache.get(key);
    }

    public Flux<List<Parameter>> changes() {
        return cache.changes();
    }

    public void register(Parameter parameter) {
        cache.put(parameter.key(), parameter);
    }

    public void remove(String key) {
        cache.remove(key);
    }

    private void startPolling() {
        pollingSubscription = Flux.interval(pollingInterval)
                .flatMap(tick -> refreshFromDatabase("polling"))
                .subscribe();
    }
}
