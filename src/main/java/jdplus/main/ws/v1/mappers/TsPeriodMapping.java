package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.TsPeriodDto;
import jdplus.toolkit.base.api.timeseries.TsPeriod;
import jdplus.toolkit.base.api.timeseries.TsUnit;

import java.time.LocalDate;

public class TsPeriodMapping {
    public static TsPeriodDto toDto(TsPeriod value) {
        return TsPeriodDto
                .newBuilder()
                .setFrequency(TsUnitMapping.toDto(value.getUnit()))
                .setYear(value.year())
                .setPos(value.annualPosition())
                .build();
    }

    public static TsPeriod toModel(TsPeriodDto value) {
        TsUnit unit = EnumsMapping.toModel(value.getFrequency());
        return TsPeriod.of(
                unit,
                LocalDate.of(value.getYear(), 1, 1).plus(value.getPos() * unit.getAmount(), unit.getChronoUnit())
        );
    }
}
