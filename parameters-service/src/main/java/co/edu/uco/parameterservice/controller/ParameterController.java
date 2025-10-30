package co.edu.uco.parameterservice.controller;

import co.edu.uco.parameterservice.model.Parameter;
import co.edu.uco.parameterservice.model.ParameterRequest;
import co.edu.uco.parameterservice.service.ReactiveParameterService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/parameters")
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
    public Mono<ResponseEntity<Parameter>> create(@Valid @RequestBody Mono<Parameter> request) {
        return request.flatMap(parameter -> service.upsert(parameter.key(), parameter.value()))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{key}")
    public Mono<ResponseEntity<Parameter>> upsert(@PathVariable String key,
                                                  @Valid @RequestBody Mono<ParameterRequest> request) {
        return request.flatMap(body -> service.upsert(key, body.value()))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    }

    @DeleteMapping("/{key}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String key) {
        return service.delete(key)
                .map(deleted -> deleted
                        ? ResponseEntity.noContent().<Void>build()
                        : ResponseEntity.notFound().build());
    }
}
