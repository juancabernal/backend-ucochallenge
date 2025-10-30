package co.edu.uco.parametersservice.domain.event;

import co.edu.uco.parametersservice.domain.model.Parameter;

public record ParameterChange(Type type, Parameter payload) {

    public enum Type {
        CREATED,
        UPDATED,
        DELETED
    }
}
