package co.edu.uco.parametersservice.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import co.edu.uco.parametersservice.infrastructure.persistence.entity.ParameterEntity;
import reactor.core.publisher.Mono;

@Repository
public interface ParameterRepository extends ReactiveCrudRepository<ParameterEntity, UUID> {

    Mono<ParameterEntity> findByCode(String code);

    Mono<Boolean> existsByCode(String code);

    Mono<Void> deleteByCode(String code);
}
