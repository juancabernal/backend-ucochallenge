package co.edu.uco.ucochallenge.application.user.registerUser.usecase.impl;

import org.springframework.stereotype.Service;
import co.edu.uco.ucochallenge.domain.user.port.out.UserRepository;
import co.edu.uco.ucochallenge.application.user.registerUser.usecase.RegisterUserUseCase;
import co.edu.uco.ucochallenge.crosscuting.exception.DomainException;
import co.edu.uco.ucochallenge.domain.user.model.User;


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
                        throw DomainException.build("email already registered", "El correo electrónico ya se encuentra registrado.");
                }

                if (repository.existsByIdTypeAndIdNumber(domain.idType(), domain.idNumber())) {
                        throw DomainException.build("idNumber already registered", "Ya existe un usuario registrado con el documento proporcionado.");
                }

                if (repository.existsByMobileNumber(domain.mobileNumber())) {
                        throw DomainException.build("mobileNumber already registered", "El número de teléfono móvil ya se encuentra registrado.");
                }
        }

}
