package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.ObsGatheringDto;
import jdplus.toolkit.base.api.timeseries.util.ObsGathering;

public class ObsGatheringMapping {
    public static ObsGatheringDto toDto(ObsGathering value) {
        return ObsGatheringDto
                .newBuilder()
                .setFrequency(TsUnitMapping.toDto(value.getUnit()))
                .setAggregationType(EnumsMapping.toDto(value.getAggregationType()))
                .setAllowPartialAggregation(value.isAllowPartialAggregation())
                .setIncludeMissingValues(value.isIncludeMissingValues())
                .build();
    }

    public static ObsGathering toModel(ObsGatheringDto value) {
        return ObsGathering
                .builder()
                .unit(EnumsMapping.toModel(value.getFrequency()))
                .aggregationType(EnumsMapping.toModel(value.getAggregationType()))
                .allowPartialAggregation(value.getAllowPartialAggregation())
                .includeMissingValues(value.getIncludeMissingValues())
                .build();
    }
}
