package co.edu.uco.ucochallenge.application.user.updateUser.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.application.hateoas.LinkDTO;
import co.edu.uco.ucochallenge.application.user.updateUser.dto.UpdateUserInputDTO;
import co.edu.uco.ucochallenge.application.user.updateUser.dto.UpdateUserOutputDTO;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.domain.user.model.User;

@Component
public class UpdateUserMapper {

        public User toDomain(final UUID id, final UpdateUserInputDTO dto) {
                return new User(
                                id,
                                dto.idType(),
                                dto.idNumber(),
                                dto.firstName(),
                                dto.secondName(),
                                dto.firstSurname(),
                                dto.secondSurname(),
                                dto.homeCity(),
                                dto.email(),
                                dto.mobileNumber(),
                                false,
                                false);
        }

        public UpdateUserOutputDTO toOutput(final User user, final List<LinkDTO> links) {
                return UpdateUserOutputDTO.of(user.id(), buildFullName(user), user.email(), links);
        }

        private String buildFullName(final User user) {
                return Stream.of(user.firstName(), user.secondName(), user.firstSurname(), user.secondSurname())
                                .filter(name -> !TextHelper.isEmpty(name))
                                .collect(Collectors.joining(" "));
        }
}
