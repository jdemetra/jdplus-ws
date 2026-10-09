package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.DiffuseLikelihoodStatisticsDto;
import jdplus.main.ws.v1.LikelihoodStatisticsDto;
import jdplus.toolkit.base.core.stats.likelihood.DiffuseLikelihoodStatistics;
import jdplus.toolkit.base.core.stats.likelihood.LikelihoodStatistics;

public class LikelihoodStatisticsMapping {
    public static LikelihoodStatisticsDto toDto(LikelihoodStatistics ls){
        return LikelihoodStatisticsDto.newBuilder()
                .setNobs(ls.getObservationsCount())
                .setNeffectiveobs(ls.getEffectiveObservationsCount())
                .setNparams(ls.getEstimatedParametersCount())
                .setDegreesOfFreedom(ls.getEffectiveObservationsCount() - ls.getEstimatedParametersCount())
                .setLogLikelihood(ls.getLogLikelihood())
                .setAdjustedLogLikelihood(ls.getAdjustedLogLikelihood())
                .setAic(ls.getAIC())
                .setAicc(ls.getAICC())
                .setBic(ls.getBIC())
                .setBicc(ls.getBICC())
                .setBic2(ls.getBIC2())
                .setHannanQuinn(ls.getHannanQuinn())
                .setSsq(ls.getSsqErr())
                .build();
    }

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
