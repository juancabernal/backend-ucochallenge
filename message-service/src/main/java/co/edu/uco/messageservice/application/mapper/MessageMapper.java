package co.edu.uco.messageservice.application.mapper;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import co.edu.uco.messageservice.domain.model.Message;
import co.edu.uco.messageservice.infrastructure.persistence.entity.MessageEntity;

@Component
public class MessageMapper {

    public Message toDomain(MessageEntity entity) {
        return new Message(entity.getId(), entity.getCode(), entity.getValue(), entity.getUpdatedAt());
    }

    public MessageEntity toEntity(Message message) {
        MessageEntity entity = new MessageEntity();
        entity.setId(message.id());
        entity.setCode(message.code());
        entity.setValue(message.value());
        entity.setUpdatedAt(message.updatedAt());
        return entity;
    }

    public Message from(String code, String value) {
        return new Message(null, code, value, Instant.now());
    }

    public Message withUpdatedValue(Message original, String value) {
        return new Message(original.id(), original.code(), value, Instant.now());
    }

    public Message withGeneratedId(Message message) {
        UUID id = message.id() != null ? message.id() : UUID.randomUUID();
        return new Message(id, message.code(), message.value(), message.updatedAt());
    }
}
