package co.edu.uco.messageservice.controller;

import co.edu.uco.messageservice.model.Message;
import co.edu.uco.messageservice.model.MessageRequest;
import co.edu.uco.messageservice.service.ReactiveMessageService;
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

/**
 * Controlador REST totalmente reactivo basado en WebFlux.
 */
@RestController
@RequestMapping("/messages")
@Validated
public class MessageController {

    private final ReactiveMessageService service;

    public MessageController(ReactiveMessageService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Message> findAll() {
        return service.findAll();
    }

    @GetMapping("/{key}")
    public Mono<ResponseEntity<Message>> findByKey(@PathVariable String key) {
        return service.findByKey(key)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Message>> create(@Valid @RequestBody Mono<Message> request) {
        return request.flatMap(message -> service.upsert(message.key(), message.value()))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/{key}")
    public Mono<ResponseEntity<Message>> upsert(@PathVariable String key,
                                                @Valid @RequestBody Mono<MessageRequest> request) {
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
