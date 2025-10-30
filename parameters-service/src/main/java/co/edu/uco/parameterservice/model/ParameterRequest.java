package co.edu.uco.parameterservice.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Request mínima para crear o actualizar un parámetro.
 */
public record ParameterRequest(
        @NotBlank(message = "El valor del parámetro es obligatorio")
        String value
) {
}
