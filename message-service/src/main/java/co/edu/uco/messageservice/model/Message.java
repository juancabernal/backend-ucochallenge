package co.edu.uco.messageservice.model;

import java.util.Objects;

/**
 * Immutable representation of a catalog message.
 */
public record Message(String key, String value) {

    public Message {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(value, "value must not be null");
    }
}
