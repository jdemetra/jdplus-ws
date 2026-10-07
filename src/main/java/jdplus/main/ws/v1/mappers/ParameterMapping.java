package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.ParameterDto;
import jdplus.toolkit.base.api.data.Parameter;

import java.util.List;

public class ParameterMapping {
    public static ParameterDto toDto(Parameter value) {
        ParameterDto.Builder result = ParameterDto
                .newBuilder()
                .setValue(value.getValue())
                .setType(EnumsMapping.toDto(value.getType()));
        return result.build();
    }

    public static ParameterDto toDto(Parameter value, String description) {
        ParameterDto.Builder result = ParameterDto
                .newBuilder()
                .setValue(value.getValue())
                .setType(EnumsMapping.toDto(value.getType()))
                .setDescription(description);
        return result.build();
    }

    public static Parameter toModel(ParameterDto dto) {
        return switch (dto.getType()) {
            case PARAMETER_FIXED -> Parameter.fixed(dto.getValue());
            case PARAMETER_INITIAL -> Parameter.initial(dto.getValue());
            case PARAMETER_ESTIMATED -> Parameter.estimated(dto.getValue());
            case PARAMETER_UNDEFINED -> Parameter.undefined();
            default -> null;
        };
    }

    public static Parameter[] toModel(List<ParameterDto> dtos) {
        return dtos.stream()
                .map(ParameterMapping::toModel)
                .toArray(Parameter[]::new);
    }
}
