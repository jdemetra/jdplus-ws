package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.TemporalDisaggregationResultsDto;

import java.util.Arrays;

public class TemporalDisaggregationMapping {
    public static TemporalDisaggregationResultsDto toDto(jdplus.benchmarking.base.core.univariate.TemporalDisaggregationResults value) {
        TemporalDisaggregationResultsDto.Builder result = TemporalDisaggregationResultsDto
                .newBuilder()
                .setOriginalSeries(TsDataMapping.toDto(value.getOriginalSeries()))
                .setDisaggregationDomain(TsDomainMapping.toDto(value.getDisaggregationDomain()))
                .setHyperParametersCount(value.getHyperParametersCount())
                .setLikelihood(DiffuseConcentratedLikelihoodMapping.toDto(value.getLikelihood()))
                .setStats(DiffuseLikelihoodStatisticsMapping.toDto(value.getStats()))
                .setResidualsDiagnostics(ResidualsDiagnosticsMapping.toDto(value.getResidualsDiagnostics()))
                .setDisaggregatedSeries(TsDataMapping.toDto(value.getDisaggregatedSeries()))
                .setStDevDisaggregatedSeries(TsDataMapping.toDto(value.getStdevDisaggregatedSeries()));

        if (value.getMaximum() != null)
            result.setMaximum(ObjectiveFunctionPointMapping.toDto(value.getMaximum()));

        if (value.getRegressionEffects() != null)
            result.setRegressionEffects(TsDataMapping.toDto(value.getRegressionEffects()));

        Arrays.stream(value.getIndicators())
                .forEach(indicator -> result.addIndicators(VariableMapping.toDto(indicator)));

        return result.build();
    }
}
