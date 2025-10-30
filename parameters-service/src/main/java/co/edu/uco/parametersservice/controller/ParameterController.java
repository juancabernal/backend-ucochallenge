package co.edu.uco.parametersservice.controller;

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

import co.edu.uco.parametersservice.application.service.ParameterService;
import co.edu.uco.parametersservice.controller.dto.ParameterRequest;
import co.edu.uco.parametersservice.controller.dto.ParameterUpdateRequest;
import co.edu.uco.parametersservice.domain.event.ParameterChange;
import co.edu.uco.parametersservice.domain.model.Parameter;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/parameters")
@Validated
public class ParameterController {

    private final ParameterService parameterService;

    public ParameterController(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @GetMapping
    public Flux<Parameter> getParameters() {
        return parameterService.findAll();
    }

    @GetMapping("/{key}")
    public Mono<Parameter> getParameterByKey(@PathVariable String key) {
        return parameterService.findByKey(key);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Parameter> createParameter(@Valid @RequestBody Mono<ParameterRequest> requestMono) {
        return requestMono.flatMap(request -> parameterService.createParameter(request.key(), request.value()));
    }

    @PutMapping("/{key}")
    public Mono<Parameter> updateParameter(@PathVariable String key,
            @Valid @RequestBody Mono<ParameterUpdateRequest> requestMono) {
        return requestMono.flatMap(request -> parameterService.updateParameter(key, request.value()));
    }

    @DeleteMapping("/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteParameter(@PathVariable String key) {
        return parameterService.deleteParameter(key).then();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ParameterChange>> streamParameterChanges() {
        return parameterService.streamChanges().map(change -> ServerSentEvent.<ParameterChange>builder()
                .event(change.type().name())
                .data(change)
                .build());
    }

    @GetMapping(value = "/snapshots", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<java.util.List<Parameter>>> streamCacheSnapshots() {
        return parameterService.streamCacheSnapshots().map(snapshot -> ServerSentEvent.<java.util.List<Parameter>>builder()
                .event("CACHE_REFRESH")
                .data(snapshot)
                .build());
    }
}
