package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.ObjectiveFunctionPointDto;
import jdplus.toolkit.base.api.math.functions.ObjectiveFunctionPoint;

import java.util.Arrays;

public class ObjectiveFunctionPointMapping {
    public static ObjectiveFunctionPointDto toDto(ObjectiveFunctionPoint value) {
        ObjectiveFunctionPointDto.Builder result = ObjectiveFunctionPointDto
                .newBuilder()
                .setValue(value.getValue())
                .setHessian(MatrixMapping.toDto(value.getHessian()));
        Arrays.stream(value.getParameters()).forEach(result::addParameters);
        Arrays.stream(value.getGradient()).forEach(result::addGradient);

        return result.build();
    }
}
