package co.edu.uco.messageservice.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Representa un mensaje simple identificado por una clave.
 */
public record Message(
        @NotBlank(message = "La clave del mensaje es obligatoria")
        String key,
        @NotBlank(message = "El valor del mensaje es obligatorio")
        String value
) {
}
