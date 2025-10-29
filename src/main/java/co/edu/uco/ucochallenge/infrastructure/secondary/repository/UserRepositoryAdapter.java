package co.edu.uco.ucochallenge.infrastructure.secondary.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.domain.user.model.User;
import co.edu.uco.ucochallenge.domain.user.port.out.UserRepository;
import co.edu.uco.ucochallenge.infrastructure.secondary.repository.entity.UserEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.repository.mapper.UserEntityMapper;

@Component
public class UserRepositoryAdapter implements UserRepository {

        private final UserJpaRepository jpaRepository;
        private final UserEntityMapper mapper;

        public UserRepositoryAdapter(final UserJpaRepository jpaRepository, final UserEntityMapper mapper) {
                this.jpaRepository = jpaRepository;
                this.mapper = mapper;
        }

        @Override
        public boolean existsByEmail(final String email) {
                return jpaRepository.existsByEmailIgnoreCase(email);
        }

        @Override
        public boolean existsByIdTypeAndIdNumber(final UUID idType, final String idNumber) {
                return jpaRepository.existsByIdTypeIdAndIdNumber(idType, idNumber);
        }

        @Override
        public boolean existsByMobileNumber(final String mobileNumber) {
                return jpaRepository.existsByMobileNumber(mobileNumber);
        }

        @Override
        public User save(final User user) {
                final UserEntity entity = mapper.toEntity(user);
                final UserEntity savedEntity = jpaRepository.save(entity);
                return mapper.toDomain(savedEntity);
        }

        @Override
        public List<User> findAll() {
                return mapper.toDomainList(jpaRepository.findAll());
        }
}
