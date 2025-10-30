package co.edu.uco.messageservice.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequest(
        @NotBlank(message = "Text is required") String text,
        @NotBlank(message = "Language is required") String language) {
}
