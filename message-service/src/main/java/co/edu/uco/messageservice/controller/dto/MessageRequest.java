package co.edu.uco.messageservice.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(
        @NotBlank(message = "Code is required") String code,
        @NotBlank(message = "Text is required") String text,
        @NotBlank(message = "Language is required") String language) {
}
