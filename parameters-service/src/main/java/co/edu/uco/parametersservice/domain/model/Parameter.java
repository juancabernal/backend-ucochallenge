package co.edu.uco.parametersservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Parameter(UUID id, String code, String value, Instant updatedAt) {
    public Parameter {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Parameter code must not be blank");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Parameter value must not be blank");
        }
    }
}
