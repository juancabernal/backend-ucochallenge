package co.edu.uco.messageservice.domain.event;

import co.edu.uco.messageservice.domain.model.Message;

public record MessageChange(Type type, Message payload) {

    public enum Type {
        CREATED,
        UPDATED,
        DELETED
    }
}
