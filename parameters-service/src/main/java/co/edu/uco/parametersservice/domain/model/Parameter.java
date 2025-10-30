package co.edu.uco.parametersservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Parameter(UUID id, String key, String value, Instant updatedAt) {
    public Parameter {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Parameter key must not be blank");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Parameter value must not be blank");
        }
    }
}
