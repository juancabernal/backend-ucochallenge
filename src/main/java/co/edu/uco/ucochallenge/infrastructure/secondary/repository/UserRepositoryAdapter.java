package co.edu.uco.ucochallenge.infrastructure.secondary.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.domain.user.model.User;
import co.edu.uco.ucochallenge.domain.user.port.out.UserRepository;
import co.edu.uco.ucochallenge.crosscuting.exception.DomainException;
import co.edu.uco.ucochallenge.infrastructure.secondary.repository.entity.UserEntity;
import co.edu.uco.ucochallenge.infrastructure.secondary.repository.mapper.UserEntityMapper;

@Component
public class UserRepositoryAdapter implements UserRepository {

        private final UserJpaRepository jpaRepository;
        private final UserEntityMapper mapper;
        private final CityJpaRepository cityJpaRepository;
        private final IdTypeJpaRepository idTypeJpaRepository;

        public UserRepositoryAdapter(final UserJpaRepository jpaRepository, final UserEntityMapper mapper,
                        final CityJpaRepository cityJpaRepository, final IdTypeJpaRepository idTypeJpaRepository) {
                this.jpaRepository = jpaRepository;
                this.mapper = mapper;
                this.cityJpaRepository = cityJpaRepository;
                this.idTypeJpaRepository = idTypeJpaRepository;
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
                validateReferences(user);
                final UserEntity entity = mapper.toEntity(user);
                final UserEntity savedEntity = jpaRepository.save(entity);
                return mapper.toDomain(savedEntity);
        }

        private void validateReferences(final User user) {
                if (!idTypeJpaRepository.existsById(user.idType())) {
                        throw DomainException.build("idType does not exist",
                                        "El tipo de identificación proporcionado no existe.");
                }

                if (!cityJpaRepository.existsById(user.homeCity())) {
                        throw DomainException.build("homeCity does not exist",
                                        "La ciudad de residencia proporcionada no existe.");
                }
        }

        @Override
        public List<User> findAll() {
                return mapper.toDomainList(jpaRepository.findAll());
        }
}
