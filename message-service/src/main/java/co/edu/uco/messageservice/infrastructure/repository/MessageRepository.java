package co.edu.uco.messageservice.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import co.edu.uco.messageservice.infrastructure.persistence.entity.MessageEntity;
import reactor.core.publisher.Mono;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<MessageEntity, UUID> {

    Mono<MessageEntity> findByCode(String code);

    Mono<Boolean> existsByCode(String code);

    Mono<Void> deleteByCode(String code);
}
