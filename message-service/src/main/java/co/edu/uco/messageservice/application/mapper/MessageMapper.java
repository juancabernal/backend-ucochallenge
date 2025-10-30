package co.edu.uco.messageservice.application.mapper;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import co.edu.uco.messageservice.domain.model.Message;
import co.edu.uco.messageservice.infrastructure.persistence.entity.MessageEntity;

@Component
public class MessageMapper {

    public Message toDomain(MessageEntity entity) {
        return new Message(entity.getId(), entity.getCode(), entity.getText(), entity.getLanguage(),
                entity.getUpdatedAt());
    }

    public MessageEntity toEntity(Message message) {
        MessageEntity entity = new MessageEntity();
        entity.setId(message.id());
        entity.setCode(message.code());
        entity.setText(message.text());
        entity.setLanguage(message.language());
        entity.setUpdatedAt(message.updatedAt());
        return entity;
    }

    public Message from(String code, String text, String language) {
        return new Message(null, code, text, language, Instant.now());
    }

    public Message withUpdatedValues(Message original, String text, String language) {
        return new Message(original.id(), original.code(), text, language, Instant.now());
    }

    public Message withGeneratedId(Message message) {
        UUID id = message.id() != null ? message.id() : UUID.randomUUID();
        return new Message(id, message.code(), message.text(), message.language(), message.updatedAt());
    }
}
