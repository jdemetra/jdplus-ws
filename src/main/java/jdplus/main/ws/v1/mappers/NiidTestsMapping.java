package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.NiidTestsDto;
import jdplus.toolkit.base.core.stats.tests.NiidTests;

public class NiidTestsMapping {
    public static NiidTestsDto toDto(NiidTests value) {
        NiidTestsDto.Builder result = NiidTestsDto
                .newBuilder()
                .setMean(StatisticalTestMapping.toDto(value.meanTest()))
                .setSkewness(StatisticalTestMapping.toDto(value.skewness()))
                .setKurtosis(StatisticalTestMapping.toDto(value.kurtosis()))
                .setDoornikHansen(StatisticalTestMapping.toDto(value.normalityTest()))
                .setLjungBox(StatisticalTestMapping.toDto(value.ljungBox()))
                .setBoxPierce(StatisticalTestMapping.toDto(value.boxPierce()))
                .setSeasonalLjungBox(StatisticalTestMapping.toDto(value.seasonalLjungBox()))
                .setSeasonalBoxPierce(StatisticalTestMapping.toDto(value.seasonalBoxPierce()))
                .setRunsNumber(StatisticalTestMapping.toDto(value.runsNumber()))
                .setRunsLength(StatisticalTestMapping.toDto(value.runsLength()))
                .setUpDownRunsNumber(StatisticalTestMapping.toDto(value.upAndDownRunsNumbber()))
                .setUpDownRunsLength(StatisticalTestMapping.toDto(value.upAndDownRunsLength()))
                .setLjungBoxOnSquares(StatisticalTestMapping.toDto(value.ljungBoxOnSquare()))
                .setBoxPierceOnSquares(StatisticalTestMapping.toDto(value.boxPierceOnSquare()));

        return result.build();
    }
}
