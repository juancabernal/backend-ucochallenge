package co.edu.uco.ucochallenge.application.user.registerUser.usecase.impl;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.application.user.registerUser.usecase.RegisterUserUseCase;
import co.edu.uco.ucochallenge.crosscuting.exception.DomainException;
import co.edu.uco.ucochallenge.domain.user.model.User;
import co.edu.uco.ucochallenge.domain.user.port.out.UserRepository;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

        private final UserRepository repository;

        public RegisterUserUseCaseImpl(final UserRepository repository) {
                this.repository = repository;
        }

        @Override
        public User execute(final User domain) {
                validateUniqueness(domain);
                return repository.save(domain);
        }

        private void validateUniqueness(final User domain) {
                if (repository.existsByEmail(domain.email())) {
                        throw DomainException.buildFromCatalog(MessageCodes.Domain.User.EMAIL_ALREADY_REGISTERED_TECHNICAL,
                                        MessageCodes.Domain.User.EMAIL_ALREADY_REGISTERED_USER);
                }

                if (repository.existsByIdTypeAndIdNumber(domain.idType(), domain.idNumber())) {
                        throw DomainException.buildFromCatalog(
                                        MessageCodes.Domain.User.ID_NUMBER_ALREADY_REGISTERED_TECHNICAL,
                                        MessageCodes.Domain.User.ID_NUMBER_ALREADY_REGISTERED_USER);
                }

                if (repository.existsByMobileNumber(domain.mobileNumber())) {
                        throw DomainException.buildFromCatalog(MessageCodes.Domain.User.MOBILE_ALREADY_REGISTERED_TECHNICAL,
                                        MessageCodes.Domain.User.MOBILE_ALREADY_REGISTERED_USER);
                }
        }
}
