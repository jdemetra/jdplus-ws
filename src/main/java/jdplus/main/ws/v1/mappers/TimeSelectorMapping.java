package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.TimeSelectorDto;
import jdplus.toolkit.base.api.timeseries.TimeSelector;

public class TimeSelectorMapping {
    public static TimeSelector toModel(TimeSelectorDto dto) {
        return TimeSelector.builder().type(EnumsMapping.toModel(dto.getType()))
                .n0(dto.getN0())
                .n1(dto.getN1())
                .d0(LocalDateTimeMapping.toModel(dto.getD0()))
                .d1(LocalDateTimeMapping.toModel(dto.getD1()))
                .build();
    }
}
