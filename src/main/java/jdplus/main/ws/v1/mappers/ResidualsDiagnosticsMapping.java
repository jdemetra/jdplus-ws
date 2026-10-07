package jdplus.main.ws.v1.mappers;

import jdplus.benchmarking.base.core.univariate.ResidualsDiagnostics;
import jdplus.main.ws.v1.ResidualsDiagnosticsDto;

public class ResidualsDiagnosticsMapping {
    public static ResidualsDiagnosticsDto toDto(ResidualsDiagnostics value) {
        ResidualsDiagnosticsDto.Builder result = ResidualsDiagnosticsDto
                .newBuilder()
                .setFullResiduals(TsDataMapping.toDto(value.getFullResiduals()))
                .setNiid(NiidTestsMapping.toDto(value.getNiid()));
        return result.build();
    }
}
