package co.edu.uco.parametersservice.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ParameterUpdateRequest(@NotBlank(message = "Value is required") String value) {
}
