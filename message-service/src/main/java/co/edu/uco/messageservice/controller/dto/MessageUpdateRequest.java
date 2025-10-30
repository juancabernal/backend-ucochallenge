package co.edu.uco.messageservice.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequest(@NotBlank(message = "Value is required") String value) {
}
