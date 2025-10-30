package co.edu.uco.messageservice.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(
        @NotBlank(message = "Code is required") String code,
        @NotBlank(message = "Value is required") String value) {
}
