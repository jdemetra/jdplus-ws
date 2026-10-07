package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.DiffuseConcentratedLikelihoodDto;
import jdplus.main.ws.v1.DiffuseLikelihoodStatisticsDto;
import jdplus.toolkit.base.core.stats.likelihood.DiffuseConcentratedLikelihood;
import jdplus.toolkit.base.core.stats.likelihood.DiffuseLikelihoodStatistics;

public class DiffuseLikelihoodStatisticsMapping {
    public static DiffuseLikelihoodStatisticsDto toDto(DiffuseLikelihoodStatistics value) {
        DiffuseLikelihoodStatisticsDto.Builder result = DiffuseLikelihoodStatisticsDto
                .newBuilder()
                .setNobs(value.getObservationsCount())
                .setNdiffuse(value.getDiffuseCount())
                .setNparams(value.getEstimatedParametersCount())
                .setDegreesOfFreedom(value.getObservationsCount() - value.getDiffuseCount() - value.getEstimatedParametersCount())
                .setLogLikelihood(value.getLogLikelihood())
                .setAdjustedLogLikelihood(value.getAdjustedLogLikelihood())
                .setAic(value.aic())
                .setAicc(value.aicc())
                .setBic(value.bic())
                .setSsq(value.getSsqErr())
                .setLdet(value.getLogDeterminant())
                .setDcorrection(value.getDiffuseCorrection());
        return result.build();
    }


}
