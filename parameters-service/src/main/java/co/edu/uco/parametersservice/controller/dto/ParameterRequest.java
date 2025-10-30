package co.edu.uco.parametersservice.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ParameterRequest(
        @NotBlank(message = "Key is required") String key,
        @NotBlank(message = "Value is required") String value) {
}
