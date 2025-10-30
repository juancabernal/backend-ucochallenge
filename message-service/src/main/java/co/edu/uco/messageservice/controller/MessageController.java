package co.edu.uco.messageservice.controller;

import co.edu.uco.messageservice.model.Message;
import co.edu.uco.messageservice.service.ReactiveMessageService;
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
 * Reactive REST endpoints that expose the in-memory catalog.
 * <p>
 * Example of the real-time behaviour:
 * <pre>
 *   curl -X PUT -H "Content-Type: application/json" \
 *     -d '{"key":"welcome","value":"Hola en vivo"}' \
 *     http://localhost:8082/api/messages/welcome
 *   curl http://localhost:8082/api/messages/welcome
 * </pre>
 * The second call immediately returns the updated payload without restarting the app.
 */
@RestController
@RequestMapping("/api/messages")
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
    public Mono<ResponseEntity<Message>> create(@RequestBody Mono<Message> request) {
        return request
            .flatMap(service::save)
            .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    @PutMapping("/{key}")
    public Mono<ResponseEntity<Message>> update(@PathVariable String key, @RequestBody Mono<Message> request) {
        return request
            .map(body -> new Message(key, body.value()))
            .flatMap(service::save)
            .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{key}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String key) {
        return service.delete(key)
            .then(Mono.fromSupplier(() -> ResponseEntity.noContent().<Void>build()));
    }
}
