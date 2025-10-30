package co.edu.uco.parametersservice.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ParameterRequest(
        @NotBlank(message = "Code is required") String code,
        @NotBlank(message = "Value is required") String value) {
}
