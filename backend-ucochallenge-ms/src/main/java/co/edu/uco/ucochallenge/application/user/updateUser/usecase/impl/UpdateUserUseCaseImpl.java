package co.edu.uco.ucochallenge.application.user.updateUser.usecase.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.application.user.updateUser.usecase.UpdateUserUseCase;
import co.edu.uco.ucochallenge.crosscuting.exception.DomainException;
import co.edu.uco.ucochallenge.domain.user.model.User;
import co.edu.uco.ucochallenge.domain.user.port.out.UserRepository;

@Service
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

        private final UserRepository repository;

        public UpdateUserUseCaseImpl(final UserRepository repository) {
                this.repository = repository;
        }

        @Override
        public User execute(final User changes) {
                final User current = repository.findById(changes.id())
                                .orElseThrow(() -> DomainException.build("user not found", "El usuario solicitado no existe."));

                validateUniqueness(changes);

                final User userToSave = new User(
                                changes.id(),
                                changes.idType(),
                                changes.idNumber(),
                                changes.firstName(),
                                changes.secondName(),
                                changes.firstSurname(),
                                changes.secondSurname(),
                                changes.homeCity(),
                                changes.email(),
                                changes.mobileNumber(),
                                current.emailConfirmed(),
                                current.mobileNumberConfirmed());

                return repository.save(userToSave);
        }

        private void validateUniqueness(final User changes) {
                final UUID userId = changes.id();

                if (repository.existsByEmailExcludingId(userId, changes.email())) {
                        throw DomainException.build("email already registered", "El correo electrónico ya se encuentra registrado.");
                }

                if (repository.existsByIdTypeAndIdNumberExcludingId(userId, changes.idType(), changes.idNumber())) {
                        throw DomainException.build("idNumber already registered", "Ya existe un usuario registrado con el documento proporcionado.");
                }

                if (repository.existsByMobileNumberExcludingId(userId, changes.mobileNumber())) {
                        throw DomainException.build("mobileNumber already registered", "El número de teléfono móvil ya se encuentra registrado.");
                }
        }
}
