package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.TsDomainDto;
import jdplus.toolkit.base.api.timeseries.TsDomain;

public class TsDomainMapping {
    public static TsDomainDto toDto(TsDomain value) {
        TsDomainDto.Builder result = TsDomainDto
                .newBuilder()
                .setStartPeriod(TsPeriodMapping.toDto(value.getStartPeriod()))
                .setLength(value.getLength());

        return result.build();
    }
}
