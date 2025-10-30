package co.edu.uco.ucochallenge.application.user.updateUser.mapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.uco.ucochallenge.application.hateoas.LinkDTO;
import co.edu.uco.ucochallenge.application.user.updateUser.dto.UpdateUserOutputDTO;
import co.edu.uco.ucochallenge.application.user.updateUser.interactor.UpdateUserInteractor;
import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.domain.user.model.User;

@Mapper(componentModel = "spring")
public interface UpdateUserMapper {

        @Mapping(target = "id", source = "command.id")
        @Mapping(target = "idType", source = "command.payload.idType")
        @Mapping(target = "idNumber", source = "command.payload.idNumber")
        @Mapping(target = "firstName", source = "command.payload.firstName")
        @Mapping(target = "secondName", source = "command.payload.secondName")
        @Mapping(target = "firstSurname", source = "command.payload.firstSurname")
        @Mapping(target = "secondSurname", source = "command.payload.secondSurname")
        @Mapping(target = "homeCity", source = "command.payload.homeCity")
        @Mapping(target = "email", source = "command.payload.email")
        @Mapping(target = "mobileNumber", source = "command.payload.mobileNumber")
        @Mapping(target = "emailConfirmed", constant = "false")
        @Mapping(target = "mobileNumberConfirmed", constant = "false")
        User toDomain(UpdateUserInteractor.Command command);

        @Mapping(target = "userId", source = "user.id")
        @Mapping(target = "email", source = "user.email")
        @Mapping(target = "fullName", expression = "java(buildFullName(user))")
        UpdateUserOutputDTO toOutput(User user, List<LinkDTO> links);

        default String buildFullName(final User user) {
                return Stream.of(user.firstName(), user.secondName(), user.firstSurname(), user.secondSurname())
                                .filter(name -> !TextHelper.isEmpty(name))
                                .collect(Collectors.joining(" "));
        }
}
