package co.edu.uco.ucochallenge.infrastructure.primary.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.ucochallenge.application.user.registerUser.interactor.RegisterUserInteractor;
import co.edu.uco.ucochallenge.application.user.registerUser.dto.RegisterUserInputDTO;
import co.edu.uco.ucochallenge.application.user.registerUser.dto.RegisterUserOutputDTO;
import co.edu.uco.ucochallenge.application.user.listUsers.dto.ListUsersResponseDTO;
import co.edu.uco.ucochallenge.application.user.listUsers.interactor.ListUsersInteractor;
import co.edu.uco.ucochallenge.application.Void;
import co.edu.uco.ucochallenge.infrastructure.primary.controller.response.ApiSuccessResponse;

@RestController
@RequestMapping("/uco-challenge/api/v1/users")
public class UserController {

        private final RegisterUserInteractor registerUserInteractor;
        private final ListUsersInteractor listUsersInteractor;

        public UserController(final RegisterUserInteractor registerUserInteractor, final ListUsersInteractor listUsersInteractor) {
                this.registerUserInteractor = registerUserInteractor;
                this.listUsersInteractor = listUsersInteractor;
        }

        @PostMapping
        public ResponseEntity<ApiSuccessResponse<RegisterUserOutputDTO>> registerUser(@RequestBody final RegisterUserInputDTO dto) {
                final RegisterUserOutputDTO response = registerUserInteractor.execute(dto);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiSuccessResponse.of("Usuario registrado exitosamente.", response));
        }

        @GetMapping
        public ResponseEntity<ApiSuccessResponse<ListUsersResponseDTO>> listUsers() {
                final ListUsersResponseDTO response = listUsersInteractor.execute(Void.returnVoid());
                return ResponseEntity.ok(ApiSuccessResponse.of("Usuarios obtenidos exitosamente.", response));
        }

}
