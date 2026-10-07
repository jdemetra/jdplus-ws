package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.DiffuseConcentratedLikelihoodDto;
import jdplus.toolkit.base.core.stats.likelihood.DiffuseConcentratedLikelihood;

public class DiffuseConcentratedLikelihoodMapping {
    public static DiffuseConcentratedLikelihoodDto toDto(DiffuseConcentratedLikelihood value) {
        DiffuseConcentratedLikelihoodDto.Builder result = DiffuseConcentratedLikelihoodDto
                .newBuilder()
                .setLl(value.logLikelihood())
                .setSsqerr(value.ssq())
                .setLdet(value.logDeterminant())
                // TODO: .setLddet(?)
                // TODO: .setNobs(?)
                .setNd(value.ndiffuse())
                .setNxd(value.nx()) // ?
                // TODO: .setBvar(?)
                // TODO: .setLegacy(?)
                .setScalingFactor(value.isScalingFactor());

        return result.build();
    }
}
