package co.edu.uco.messageservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Message(UUID id, String code, String value, Instant updatedAt) {
    public Message {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Message code must not be blank");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Message value must not be blank");
        }
    }
}
