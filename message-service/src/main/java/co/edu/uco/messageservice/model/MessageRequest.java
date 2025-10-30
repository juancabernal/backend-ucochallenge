package co.edu.uco.messageservice.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Representa la petición mínima necesaria para crear o actualizar un mensaje.
 */
public record MessageRequest(
        @NotBlank(message = "El valor del mensaje es obligatorio")
        String value
) {
}