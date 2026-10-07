package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.TramoOutputDto;
import jdplus.tramoseats.base.core.tramo.TramoOutput;

public class TramoOutputMapping {
    public static TramoOutputDto toDto(TramoOutput model) {
        TramoOutputDto.Builder builder = TramoOutputDto.newBuilder()
                .setEstimationSpec(SpecsMapping.toDto(model.getEstimationSpec()));

        if (model.getResult() != null) {
            builder.setResult(RegSarimaModelMapping.toDto(model.getResult()))
                    .setResultSpec(SpecsMapping.toDto(model.getResultSpec()));
        }
        // TODO detail and logs

        return builder.build();
    }
}
