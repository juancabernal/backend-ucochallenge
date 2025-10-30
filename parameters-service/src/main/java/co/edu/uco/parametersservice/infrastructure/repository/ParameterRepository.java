package co.edu.uco.parametersservice.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import co.edu.uco.parametersservice.infrastructure.persistence.entity.ParameterEntity;
import reactor.core.publisher.Mono;

@Repository
public interface ParameterRepository extends ReactiveCrudRepository<ParameterEntity, UUID> {

    Mono<ParameterEntity> findByKey(String key);

    Mono<Boolean> existsByKey(String key);

    Mono<Void> deleteByKey(String key);
}
