package jdplus.main.ws.v1;

import jdplus.benchmarking.base.core.univariate.ResidualsDiagnostics;
import jdplus.toolkit.base.api.data.AggregationType;
import jdplus.toolkit.base.api.data.DoubleSeq;
import jdplus.toolkit.base.api.data.Parameter;
import jdplus.toolkit.base.api.data.ParameterType;
import jdplus.toolkit.base.api.math.functions.ObjectiveFunctionPoint;
import jdplus.toolkit.base.api.timeseries.*;
import jdplus.toolkit.base.api.timeseries.regression.TsVariable;
import jdplus.toolkit.base.api.timeseries.util.ObsGathering;
import jdplus.toolkit.base.core.stats.likelihood.DiffuseConcentratedLikelihood;
import jdplus.toolkit.base.core.stats.likelihood.DiffuseLikelihoodStatistics;
import jdplus.toolkit.base.core.stats.tests.NiidTests;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

class Converters {

    public static ToolkitMessages.Frequency fromTsUnit(TsUnit unit) {
        if (unit.equals(TsUnit.UNDEFINED)) {
            return ToolkitMessages.Frequency.FREQ_UNDEFINED;
        }
        switch (unit.getChronoUnit()) {
            case YEARS:
                if (unit.getAmount() == 1) {
                    return ToolkitMessages.Frequency.FREQ_YEARLY;
                }
                break;
            case MONTHS:
                if (unit.getAmount() == 6) {
                    return ToolkitMessages.Frequency.FREQ_HALF_YEARLY;
                }
                if (unit.getAmount() == 4) {
                    return ToolkitMessages.Frequency.FREQ_QUADRI_MONTHLY;
                }
                if (unit.getAmount() == 3) {
                    return ToolkitMessages.Frequency.FREQ_QUARTERLY;
                }
                if (unit.getAmount() == 2) {
                    return ToolkitMessages.Frequency.FREQ_BI_MONTHLY;
                }
                if (unit.getAmount() == 1) {
                    return ToolkitMessages.Frequency.FREQ_MONTHLY;
                }
                break;
            case DAYS:
                if (unit.getAmount() == 1) {
                    return ToolkitMessages.Frequency.FREQ_DAILY;
                }
                break;
        }
        throw new IllegalArgumentException("Unsupported unit " + unit);
    }

    public static TsUnit toTsUnit(ToolkitMessages.Frequency value) {
        return switch (value) {
            case FREQ_YEARLY -> TsUnit.P1Y;
            case FREQ_HALF_YEARLY -> TsUnit.P6M;
            case FREQ_QUADRI_MONTHLY -> TsUnit.P4M;
            case FREQ_QUARTERLY -> TsUnit.P3M;
            case FREQ_BI_MONTHLY -> TsUnit.P2M;
            case FREQ_MONTHLY -> TsUnit.P1M;
            case FREQ_UNDEFINED -> TsUnit.UNDEFINED;
            case FREQ_DAILY -> TsUnit.P1D;
            default -> throw new RuntimeException("Unreachable");
        };
    }

    public static ToolkitMessages.TsPeriodDto fromTsPeriod(TsPeriod value) {
        return ToolkitMessages.TsPeriodDto
                .newBuilder()
                .setFrequency(fromTsUnit(value.getUnit()))
                .setYear(value.year())
                .setPos(value.annualPosition())
                .build();
    }

    public static TsPeriod toTsPeriod(ToolkitMessages.TsPeriodDto value) {
        TsUnit unit = toTsUnit(value.getFrequency());
        return TsPeriod.of(
                unit,
                LocalDate.of(value.getYear(), 1, 1).plus(value.getPos() * unit.getAmount(), unit.getChronoUnit())
        );
    }

    private static List<Double> fromValues(DoubleSeq value) {
        return value.stream().boxed().toList();
    }

    private static DoubleSeq toValues(List<Double> value) {
        return DoubleSeq.onMapping(value.size(), value::get);
    }

    public static ToolkitMessages.TsDataDto fromTsData(TsData value) {
        return ToolkitMessages.TsDataDto
                .newBuilder()
                .setStart(fromTsPeriod(value.getStart()))
                .addAllValues(fromValues(value.getValues()))
                .build();
    }

    public static TsData toTsData(ToolkitMessages.TsDataDto value) {
        return TsData.of(
                toTsPeriod(value.getStart()),
                toValues(value.getValuesList())
        );
    }

    public static ToolkitMessages.AggregationType fromAggregationType(AggregationType value) {
        return switch (value) {
            case None -> ToolkitMessages.AggregationType.AGGREGATION_NONE;
            case Sum -> ToolkitMessages.AggregationType.AGGREGATION_SUM;
            case Average -> ToolkitMessages.AggregationType.AGGREGATION_AVERAGE;
            case First -> ToolkitMessages.AggregationType.AGGREGATION_FIRST;
            case Last -> ToolkitMessages.AggregationType.AGGREGATION_LAST;
            case Max -> ToolkitMessages.AggregationType.AGGREGATION_MAX;
            case Min -> ToolkitMessages.AggregationType.AGGREGATION_MIN;
            default -> throw new IllegalArgumentException(value.name());
        };
    }

    public static AggregationType toAggregationType(ToolkitMessages.AggregationType value) {
        return switch (value) {
            case AGGREGATION_NONE -> AggregationType.None;
            case AGGREGATION_SUM -> AggregationType.Sum;
            case AGGREGATION_AVERAGE -> AggregationType.Average;
            case AGGREGATION_FIRST -> AggregationType.First;
            case AGGREGATION_LAST -> AggregationType.Last;
            case AGGREGATION_MAX -> AggregationType.Max;
            case AGGREGATION_MIN -> AggregationType.Min;
            default -> throw new IllegalArgumentException(value.name());
        };
    }

    public static ToolkitMessages.ParameterType fromParameterType(ParameterType value) {
        return switch (value) {
            case Undefined -> ToolkitMessages.ParameterType.PARAMETER_UNDEFINED;
            case Initial -> ToolkitMessages.ParameterType.PARAMETER_INITIAL;
            case Fixed -> ToolkitMessages.ParameterType.PARAMETER_FIXED;
            case Estimated -> ToolkitMessages.ParameterType.PARAMETER_ESTIMATED;
            default -> ToolkitMessages.ParameterType.PARAMETER_UNUSED;
        };
    }

    public static ToolkitMessages.ObsGatheringDto fromObsGathering(ObsGathering value) {
        return ToolkitMessages.ObsGatheringDto
                .newBuilder()
                .setFrequency(fromTsUnit(value.getUnit()))
                .setAggregationType(fromAggregationType(value.getAggregationType()))
                .setAllowPartialAggregation(value.isAllowPartialAggregation())
                .setIncludeMissingValues(value.isIncludeMissingValues())
                .build();
    }

    public static ObsGathering toObsGathering(ToolkitMessages.ObsGatheringDto value) {
        return ObsGathering
                .builder()
                .unit(toTsUnit(value.getFrequency()))
                .aggregationType(toAggregationType(value.getAggregationType()))
                .allowPartialAggregation(value.getAllowPartialAggregation())
                .includeMissingValues(value.getIncludeMissingValues())
                .build();
    }

    public static ToolkitMessages.DateDto fromLocalDate(LocalDate value) {
        return ToolkitMessages.DateDto
                .newBuilder()
                .setYear(value.getYear())
                .setMonth(value.getMonthValue())
                .setDay(value.getDayOfMonth())
                .build();
    }

    public static LocalDate toLocalDate(ToolkitMessages.DateDto value) {
        return LocalDate.of(value.getYear(), value.getMonth(), value.getDay());
    }

    public static ToolkitMessages.DistributionType fromDistributionType(TsDataTable.DistributionType value) {
        return switch (value) {
            case FIRST -> ToolkitMessages.DistributionType.DIST_FIRST;
            case LAST -> ToolkitMessages.DistributionType.DIST_LAST;
            case MIDDLE -> ToolkitMessages.DistributionType.DIST_MIDDLE;
        };
    }

    public static TsDataTable.DistributionType toDistributionType(ToolkitMessages.DistributionType value) {
        return switch (value) {
            case DIST_FIRST -> TsDataTable.DistributionType.FIRST;
            case DIST_LAST -> TsDataTable.DistributionType.LAST;
            case DIST_MIDDLE -> TsDataTable.DistributionType.MIDDLE;
            default -> throw new IllegalArgumentException(value.name());
        };
    }

    public static ToolkitMessages.ValueStatus fromValueStatus(TsDataTable.ValueStatus value) {
        return switch (value) {
            case PRESENT -> ToolkitMessages.ValueStatus.VS_PRESENT;
            case UNUSED -> ToolkitMessages.ValueStatus.VS_UNUSED;
            case BEFORE -> ToolkitMessages.ValueStatus.VS_BEFORE;
            case AFTER -> ToolkitMessages.ValueStatus.VS_AFTER;
            case EMPTY -> ToolkitMessages.ValueStatus.VS_EMPTY;
        };
    }

    public static TsDataTable.ValueStatus toValueStatus(ToolkitMessages.ValueStatus value) {
        return switch (value) {
            case VS_PRESENT -> TsDataTable.ValueStatus.PRESENT;
            case VS_UNUSED -> TsDataTable.ValueStatus.UNUSED;
            case VS_BEFORE -> TsDataTable.ValueStatus.BEFORE;
            case VS_AFTER -> TsDataTable.ValueStatus.AFTER;
            case VS_EMPTY -> TsDataTable.ValueStatus.EMPTY;
            default -> throw new IllegalArgumentException(value.name());
        };
    }

    public static ToolkitMessages.MatrixDto fromMatrix(jdplus.toolkit.base.api.math.matrices.Matrix value) {
        ToolkitMessages.MatrixDto.Builder result = ToolkitMessages.MatrixDto
                .newBuilder()
                .setNrows(value.getRowsCount())
                .setNcols(value.getColumnsCount());
        Arrays.stream(value.toArray()).forEach(result::addValues);
        return result.build();
    }

    public static ToolkitMessages.TemporalDisaggregationResultsDto fromTemporalDisaggregationResults(jdplus.benchmarking.base.core.univariate.TemporalDisaggregationResults value) {
        ToolkitMessages.TemporalDisaggregationResultsDto.Builder result = ToolkitMessages.TemporalDisaggregationResultsDto
                .newBuilder()
                .setOriginalSeries(fromTsData(value.getOriginalSeries()))
                .setDisaggregationDomain(fromTsDomain(value.getDisaggregationDomain()))
                .setHyperParametersCount(value.getHyperParametersCount())
                .setLikelihood(fromDiffuseConcentratedLikelihood(value.getLikelihood()))
                .setStats(fromDiffuseLikelihoodStatistics(value.getStats()))
                .setMaximum(fromObjectiveFunctionPoint(value.getMaximum()))
                .setResidualsDiagnostics(fromResidualsDiagnostics(value.getResidualsDiagnostics()))
                .setDisaggregatedSeries(fromTsData(value.getDisaggregatedSeries()))
                .setStDevDisaggregatedSeries(fromTsData(value.getStdevDisaggregatedSeries()))
                .setRegressionEffects(fromTsData(value.getRegressionEffects()));

        // TODO: indicators
//        Arrays.stream(value.getIndicators())
//                .forEach(indicator -> result.addIndicators(fromTsVariable(indicator)));
        return result.build();
    }

    public static ToolkitMessages.DiffuseConcentratedLikelihoodDto fromDiffuseConcentratedLikelihood(DiffuseConcentratedLikelihood value)
        {
        ToolkitMessages.DiffuseConcentratedLikelihoodDto.Builder result = ToolkitMessages.DiffuseConcentratedLikelihoodDto
                .newBuilder()
                .setLl( value.logLikelihood())
                .setSsqerr(value.ssq())
                .setLdet(value.logDeterminant())
                // TODO: .setLddet(?)
                // TODO: .setNobs(?)
                .setNd(value.ndiffuse())
                .setNxd(value.nx()) // ?
                // TODO: .setBvar(?)
                // TODO: .setLegacy(?)
                .setScalingFactor(value.isScalingFactor())
                ;

        return result.build();
    }

    public static ToolkitMessages.TsVariableDto fromTsVariable(TsVariable value) {
        ToolkitMessages.TsVariableDto.Builder result = ToolkitMessages.TsVariableDto
                .newBuilder()
                // TODO: .setName(?)
                .setId(value.getId())
                // TODO: .setLag(?)
                // TODO: .setCoefficient(?)
        ;
        return result.build();
    }

    public static ToolkitMessages.ParameterDto fromParameter(Parameter value) {
        ToolkitMessages.ParameterDto.Builder result = ToolkitMessages.ParameterDto
                .newBuilder()
                .setValue(value.getValue())
                // TODO: .setDescription(value.toString())
                // It is set as parameter in R converter
                .setType(fromParameterType(value.getType()));
        return result.build();
    }

    public static ToolkitMessages.DiffuseLikelihoodStatisticsDto fromDiffuseLikelihoodStatistics(DiffuseLikelihoodStatistics value) {
        ToolkitMessages.DiffuseLikelihoodStatisticsDto.Builder result = ToolkitMessages.DiffuseLikelihoodStatisticsDto
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

    public static ToolkitMessages.TsDomainDto fromTsDomain(TsDomain value) {
        ToolkitMessages.TsDomainDto.Builder result = ToolkitMessages.TsDomainDto
                .newBuilder()
                .setStartPeriod(fromTsPeriod(value.getStartPeriod()))
                .setLength(value.getLength());

        return result.build();
    }

    public static ToolkitMessages.ObjectiveFunctionPointDto fromObjectiveFunctionPoint(ObjectiveFunctionPoint value) {
        ToolkitMessages.ObjectiveFunctionPointDto.Builder result = ToolkitMessages.ObjectiveFunctionPointDto
                .newBuilder()
                .setValue(value.getValue())
                .setHessian(fromMatrix(value.getHessian()));
        Arrays.stream(value.getParameters()).forEach(result::addParameters);
        Arrays.stream(value.getGradient()).forEach(result::addGradient);

        return result.build();
    }

    public static ToolkitMessages.ResidualsDiagnosticsDto fromResidualsDiagnostics(ResidualsDiagnostics value) {
        ToolkitMessages.ResidualsDiagnosticsDto.Builder result = ToolkitMessages.ResidualsDiagnosticsDto
                .newBuilder()
                .setFullResiduals(fromTsData(value.getFullResiduals()))
                .setNiid(fromNiidTests(value.getNiid()));

        return result.build();
    }

    public static ToolkitMessages.NiidTestsDto fromNiidTests(NiidTests value) {
        ToolkitMessages.NiidTestsDto.Builder result = ToolkitMessages.NiidTestsDto
                .newBuilder()
                .setMean(fromStatisticalTest(value.meanTest()))
                .setSkewness(fromStatisticalTest(value.skewness()))
                .setKurtosis(fromStatisticalTest(value.kurtosis()))
                .setDoornikHansen(fromStatisticalTest(value.normalityTest()))
                .setLjungBox(fromStatisticalTest(value.ljungBox()))
                .setBoxPierce(fromStatisticalTest(value.boxPierce()))
                .setSeasonalLjungBox(fromStatisticalTest(value.seasonalLjungBox()))
                .setSeasonalBoxPierce(fromStatisticalTest(value.seasonalBoxPierce()))
                .setRunsNumber(fromStatisticalTest(value.runsNumber()))
                .setRunsLength(fromStatisticalTest(value.runsLength()))
                .setUpDownRunsNumber(fromStatisticalTest(value.upAndDownRunsNumbber()))
                .setUpDownRunsLength(fromStatisticalTest(value.upAndDownRunsLength()))
                .setLjungBoxOnSquares(fromStatisticalTest(value.ljungBoxOnSquare()))
                .setBoxPierceOnSquares(fromStatisticalTest(value.boxPierceOnSquare()));

        return result.build();
    }

    public static ToolkitMessages.StatisticalTestDto fromStatisticalTest(jdplus.toolkit.base.api.stats.StatisticalTest value) {
        ToolkitMessages.StatisticalTestDto.Builder result = ToolkitMessages.StatisticalTestDto
                .newBuilder()
                .setValue(value.getValue())
                .setPValue(value.getPvalue())
                .setDescription(value.getDescription());
        return result.build();
    }
}
