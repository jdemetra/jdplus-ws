package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.ParametersEstimationDto;
import jdplus.toolkit.base.api.data.Iterables;
import jdplus.toolkit.base.api.data.ParametersEstimation;

public class ParametersEstimationMapping {
    public static ParametersEstimationDto toDto(ParametersEstimation p) {
        ParametersEstimationDto.Builder builder = ParametersEstimationDto.newBuilder()
                .addAllValue(Iterables.of(p.getValues()))
                .addAllScore(Iterables.of(p.getScores()))
                .setCovariance(MatrixMapping.toDto(p.getCovariance()));

        String description = p.getDescription();
        if (description != null) {
            builder.setDescription(description);
        }
        return builder.build();
    }
}
