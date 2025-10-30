package co.edu.uco.parametersservice.model;

import java.util.Objects;

/**
 * Immutable configuration parameter.
 */
public record Parameter(String key, String value) {

    public Parameter {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(value, "value must not be null");
    }
}
