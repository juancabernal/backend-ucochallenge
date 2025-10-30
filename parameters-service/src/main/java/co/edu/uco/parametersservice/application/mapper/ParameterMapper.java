package co.edu.uco.parametersservice.application.mapper;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import co.edu.uco.parametersservice.domain.model.Parameter;
import co.edu.uco.parametersservice.infrastructure.persistence.entity.ParameterEntity;

@Component
public class ParameterMapper {

    public Parameter toDomain(ParameterEntity entity) {
        return new Parameter(entity.getId(), entity.getCode(), entity.getValue(), entity.getUpdatedAt());
    }

    public ParameterEntity toEntity(Parameter parameter) {
        ParameterEntity entity = new ParameterEntity();
        entity.setId(parameter.id());
        entity.setCode(parameter.code());
        entity.setValue(parameter.value());
        entity.setUpdatedAt(parameter.updatedAt());
        return entity;
    }

    public Parameter from(String code, String value) {
        return new Parameter(null, code, value, Instant.now());
    }

    public Parameter withUpdatedValue(Parameter original, String value) {
        return new Parameter(original.id(), original.code(), value, Instant.now());
    }

    public Parameter withGeneratedId(Parameter parameter) {
        UUID id = parameter.id() != null ? parameter.id() : UUID.randomUUID();
        return new Parameter(id, parameter.code(), parameter.value(), parameter.updatedAt());
    }
}
