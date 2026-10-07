package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.ParameterDto;
import jdplus.toolkit.base.api.data.Parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParameterMapping {
    public static ParameterDto toDto(Parameter value) {
        if (value == null) {
            return ParameterDto.getDefaultInstance();
        }

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

    public static List<ParameterDto> toDto(Parameter[] p) {
        if (p == null || p.length == 0) {
            return Collections.emptyList();
        }
        ArrayList<ParameterDto> list = new ArrayList<>();
        for (Parameter parameter : p) {
            list.add(toDto(parameter));
        }
        return list;
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

    public static Parameter[] toModel(List<ParameterDto> p) {
        int n = p.size();
        if (n == 0) {
            return null;
        } else {
            Parameter[] np = new Parameter[n];
            for (int i = 0; i < n; ++i) {
                Parameter c = toModel(p.get(i));
                if (c == null)
                    np[i]=Parameter.undefined();
                else
                    np[i]=c;
            }
            return np;
        }
    }
}
