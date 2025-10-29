package co.edu.uco.ucochallenge.application.user.updateUser.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.uco.ucochallenge.application.hateoas.LinkDTO;
import co.edu.uco.ucochallenge.application.user.updateUser.dto.UpdateUserInputDTO;
import co.edu.uco.ucochallenge.application.user.updateUser.dto.UpdateUserOutputDTO;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.domain.user.model.User;

@Mapper(componentModel = "spring")
public interface UpdateUserMapper {

        @Mapping(target = "id", source = "id")
        @Mapping(target = "idType", source = "dto.idType")
        @Mapping(target = "idNumber", source = "dto.idNumber")
        @Mapping(target = "firstName", source = "dto.firstName")
        @Mapping(target = "secondName", source = "dto.secondName")
        @Mapping(target = "firstSurname", source = "dto.firstSurname")
        @Mapping(target = "secondSurname", source = "dto.secondSurname")
        @Mapping(target = "homeCity", source = "dto.homeCity")
        @Mapping(target = "email", source = "dto.email")
        @Mapping(target = "mobileNumber", source = "dto.mobileNumber")
        @Mapping(target = "emailConfirmed", expression = "java(false)")
        @Mapping(target = "mobileNumberConfirmed", expression = "java(false)")
        User toDomain(UUID id, UpdateUserInputDTO dto);

        default UpdateUserOutputDTO toOutput(final User user, final List<LinkDTO> links) {
                return UpdateUserOutputDTO.of(user.id(), buildFullName(user), user.email(), links);
        }

        default String buildFullName(final User user) {
                return Stream.of(user.firstName(), user.secondName(), user.firstSurname(), user.secondSurname())
                                .filter(name -> !TextHelper.isEmpty(name))
                                .collect(Collectors.joining(" "));
        }
}
