package co.edu.uco.messageservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.messageservice.application.service.MessageService;
import co.edu.uco.messageservice.controller.dto.MessageRequest;
import co.edu.uco.messageservice.controller.dto.MessageUpdateRequest;
import co.edu.uco.messageservice.domain.event.MessageChange;
import co.edu.uco.messageservice.domain.model.Message;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/messages")
@Validated
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public Flux<Message> getMessages() {
        return messageService.findAll();
    }

    @GetMapping("/{code}")
    public Mono<Message> getMessageByCode(@PathVariable String code) {
        return messageService.findByCode(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Message> createMessage(@Valid @RequestBody Mono<MessageRequest> requestMono) {
        return requestMono.flatMap(request -> messageService.createMessage(request.code(), request.value()));
    }

    @PutMapping("/{code}")
    public Mono<Message> updateMessage(@PathVariable String code,
            @Valid @RequestBody Mono<MessageUpdateRequest> requestMono) {
        return requestMono.flatMap(request -> messageService.updateMessage(code, request.value()));
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMessage(@PathVariable String code) {
        return messageService.deleteMessage(code).then();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<MessageChange>> streamMessageChanges() {
        return messageService.streamChanges().map(change -> ServerSentEvent.<MessageChange>builder()
                .event(change.type().name())
                .data(change)
                .build());
    }
}
