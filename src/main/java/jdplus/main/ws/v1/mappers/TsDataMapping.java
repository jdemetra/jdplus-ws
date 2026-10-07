package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.TsDataDto;
import jdplus.toolkit.base.api.timeseries.TsData;

public class TsDataMapping {
    public static TsDataDto toDto(TsData value) {
        return TsDataDto
                .newBuilder()
                .setStart(TsPeriodMapping.toDto(value.getStart()))
                .addAllValues(DoublesMapping.toDto(value.getValues()))
                .build();
    }

    public static TsData toModel(TsDataDto value) {
        return TsData.of(
                TsPeriodMapping.toModel(value.getStart()),
                DoublesMapping.toModel(value.getValuesList())
        );
    }
}
