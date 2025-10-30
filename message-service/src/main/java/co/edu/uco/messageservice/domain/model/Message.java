package co.edu.uco.messageservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Message(UUID id, String code, String text, String language, Instant updatedAt) {
    public Message {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Message code must not be blank");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Message text must not be blank");
        }
        if (language == null || language.isBlank()) {
            throw new IllegalArgumentException("Message language must not be blank");
        }
    }
}
