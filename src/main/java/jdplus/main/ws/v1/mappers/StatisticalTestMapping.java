package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.StatisticalTestDto;
import jdplus.toolkit.base.api.stats.StatisticalTest;

public class StatisticalTestMapping {
    public static StatisticalTestDto toDto(StatisticalTest value) {
        StatisticalTestDto.Builder result = StatisticalTestDto
                .newBuilder()
                .setValue(value.getValue())
                .setPValue(value.getPvalue())
                .setDescription(value.getDescription());
        return result.build();
    }
}
