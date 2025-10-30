package co.edu.uco.parametersservice.controller;

import co.edu.uco.parametersservice.model.Parameter;
import co.edu.uco.parametersservice.service.ReactiveParameterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive REST endpoints for configuration parameters.
 * <p>
 * Example:
 * <pre>
 *   curl -X PUT -H "Content-Type: application/json" \
 *     -d '{"key":"ui.theme","value":"light"}' \
 *     http://localhost:8083/api/parameters/ui.theme
 *   curl http://localhost:8083/api/parameters/ui.theme
 * </pre>
 * The GET call reflects the new value instantly thanks to the reactive catalog.
 */
@RestController
@RequestMapping("/api/parameters")
@Validated
public class ParameterController {

    private final ReactiveParameterService service;

    public ParameterController(ReactiveParameterService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Parameter> findAll() {
        return service.findAll();
    }

    @GetMapping("/{key}")
    public Mono<ResponseEntity<Parameter>> findByKey(@PathVariable String key) {
        return service.findByKey(key)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Parameter>> create(@RequestBody Mono<Parameter> request) {
        return request
            .flatMap(service::save)
            .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    @PutMapping("/{key}")
    public Mono<ResponseEntity<Parameter>> update(@PathVariable String key, @RequestBody Mono<Parameter> request) {
        return request
            .map(body -> new Parameter(key, body.value()))
            .flatMap(service::save)
            .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{key}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String key) {
        return service.delete(key)
            .then(Mono.fromSupplier(() -> ResponseEntity.noContent().<Void>build()));
    }
}
