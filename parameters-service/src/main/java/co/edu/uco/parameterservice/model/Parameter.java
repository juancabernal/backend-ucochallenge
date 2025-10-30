package co.edu.uco.parameterservice.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Representa un parámetro configurable dentro del servicio.
 */
public record Parameter(
        @NotBlank(message = "La clave del parámetro es obligatoria")
        String key,
        @NotBlank(message = "El valor del parámetro es obligatorio")
        String value
) {
}
