package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.NiidTestsDto;
import jdplus.toolkit.base.core.stats.tests.NiidTests;

public class NiidTestsMapping {
    public static NiidTestsDto toDto(NiidTests value) {
        NiidTestsDto.Builder result = NiidTestsDto
                .newBuilder();

        if (value.meanTest() != null)
            result.setMean(StatisticalTestMapping.toDto(value.meanTest()));
        if (value.skewness() != null)
            result.setSkewness(StatisticalTestMapping.toDto(value.skewness()));
        if (value.kurtosis() != null)
            result.setKurtosis(StatisticalTestMapping.toDto(value.kurtosis()));
        if (value.normalityTest() != null)
            result.setDoornikHansen(StatisticalTestMapping.toDto(value.normalityTest()));
        if (value.ljungBox() != null)
            result.setLjungBox(StatisticalTestMapping.toDto(value.ljungBox()));
        if (value.boxPierce() != null)
            result.setBoxPierce(StatisticalTestMapping.toDto(value.boxPierce()));
        if (value.seasonalLjungBox() != null)
            result.setSeasonalLjungBox(StatisticalTestMapping.toDto(value.seasonalLjungBox()));
        if (value.seasonalBoxPierce() != null)
            result.setSeasonalBoxPierce(StatisticalTestMapping.toDto(value.seasonalBoxPierce()));
        if (value.runsNumber() != null)
            result.setRunsNumber(StatisticalTestMapping.toDto(value.runsNumber()));
        if (value.runsLength() != null)
            result.setRunsLength(StatisticalTestMapping.toDto(value.runsLength()));
        if (value.upAndDownRunsNumbber() != null)
            result.setUpDownRunsNumber(StatisticalTestMapping.toDto(value.upAndDownRunsNumbber()));
        if (value.upAndDownRunsLength() != null)
            result.setUpDownRunsLength(StatisticalTestMapping.toDto(value.upAndDownRunsLength()));
        if (value.ljungBoxOnSquare() != null)
            result.setLjungBoxOnSquares(StatisticalTestMapping.toDto(value.ljungBoxOnSquare()));
        if (value.boxPierceOnSquare() != null)
            result.setBoxPierceOnSquares(StatisticalTestMapping.toDto(value.boxPierceOnSquare()));

        return result.build();
    }
}
