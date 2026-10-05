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
import jdplus.tramoseats.base.api.tramo.TramoSpec;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

class Converters {

    public static Frequency fromTsUnit(TsUnit unit) {
        if (unit.equals(TsUnit.UNDEFINED)) {
            return Frequency.FREQ_UNDEFINED;
        }
        switch (unit.getChronoUnit()) {
            case YEARS:
                if (unit.getAmount() == 1) {
                    return Frequency.FREQ_YEARLY;
                }
                break;
            case MONTHS:
                if (unit.getAmount() == 6) {
                    return Frequency.FREQ_HALF_YEARLY;
                }
                if (unit.getAmount() == 4) {
                    return Frequency.FREQ_QUADRI_MONTHLY;
                }
                if (unit.getAmount() == 3) {
                    return Frequency.FREQ_QUARTERLY;
                }
                if (unit.getAmount() == 2) {
                    return Frequency.FREQ_BI_MONTHLY;
                }
                if (unit.getAmount() == 1) {
                    return Frequency.FREQ_MONTHLY;
                }
                break;
            case DAYS:
                if (unit.getAmount() == 1) {
                    return Frequency.FREQ_DAILY;
                }
                break;
        }
        throw new IllegalArgumentException("Unsupported unit " + unit);
    }

    public static TsUnit toTsUnit(Frequency value) {
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

    public static TsPeriodDto fromTsPeriod(TsPeriod value) {
        return TsPeriodDto
                .newBuilder()
                .setFrequency(fromTsUnit(value.getUnit()))
                .setYear(value.year())
                .setPos(value.annualPosition())
                .build();
    }

    public static TsPeriod toTsPeriod(TsPeriodDto value) {
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

    public static TsDataDto fromTsData(TsData value) {
        return TsDataDto
                .newBuilder()
                .setStart(fromTsPeriod(value.getStart()))
                .addAllValues(fromValues(value.getValues()))
                .build();
    }

    public static TsData toTsData(TsDataDto value) {
        return TsData.of(
                toTsPeriod(value.getStart()),
                toValues(value.getValuesList())
        );
    }

    public static jdplus.main.ws.v1.AggregationType fromAggregationType(AggregationType value) {
        return switch (value) {
            case None -> jdplus.main.ws.v1.AggregationType.AGGREGATION_NONE;
            case Sum -> jdplus.main.ws.v1.AggregationType.AGGREGATION_SUM;
            case Average -> jdplus.main.ws.v1.AggregationType.AGGREGATION_AVERAGE;
            case First -> jdplus.main.ws.v1.AggregationType.AGGREGATION_FIRST;
            case Last ->jdplus.main.ws.v1. AggregationType.AGGREGATION_LAST;
            case Max -> jdplus.main.ws.v1.AggregationType.AGGREGATION_MAX;
            case Min -> jdplus.main.ws.v1.AggregationType.AGGREGATION_MIN;
            default -> throw new IllegalArgumentException(value.name());
        };
    }

    public static AggregationType toAggregationType(jdplus.main.ws.v1.AggregationType value) {
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

    public static jdplus.main.ws.v1.ParameterType fromParameterType(ParameterType value) {
        return switch (value) {
            case Undefined -> jdplus.main.ws.v1.ParameterType.PARAMETER_UNDEFINED;
            case Initial ->jdplus.main.ws.v1. ParameterType.PARAMETER_INITIAL;
            case Fixed ->jdplus.main.ws.v1.ParameterType.PARAMETER_FIXED;
            case Estimated -> jdplus.main.ws.v1.ParameterType.PARAMETER_ESTIMATED;
            default -> jdplus.main.ws.v1.ParameterType.PARAMETER_UNUSED;
        };
    }

    public static ObsGatheringDto fromObsGathering(ObsGathering value) {
        return ObsGatheringDto
                .newBuilder()
                .setFrequency(fromTsUnit(value.getUnit()))
                .setAggregationType(fromAggregationType(value.getAggregationType()))
                .setAllowPartialAggregation(value.isAllowPartialAggregation())
                .setIncludeMissingValues(value.isIncludeMissingValues())
                .build();
    }

    public static ObsGathering toObsGathering(ObsGatheringDto value) {
        return ObsGathering
                .builder()
                .unit(toTsUnit(value.getFrequency()))
                .aggregationType(toAggregationType(value.getAggregationType()))
                .allowPartialAggregation(value.getAllowPartialAggregation())
                .includeMissingValues(value.getIncludeMissingValues())
                .build();
    }

    public static DateDto fromLocalDate(LocalDate value) {
        return DateDto
                .newBuilder()
                .setYear(value.getYear())
                .setMonth(value.getMonthValue())
                .setDay(value.getDayOfMonth())
                .build();
    }

    public static LocalDate toLocalDate(DateDto value) {
        return LocalDate.of(value.getYear(), value.getMonth(), value.getDay());
    }

    public static DistributionType fromDistributionType(TsDataTable.DistributionType value) {
        return switch (value) {
            case FIRST -> DistributionType.DIST_FIRST;
            case LAST -> DistributionType.DIST_LAST;
            case MIDDLE -> DistributionType.DIST_MIDDLE;
        };
    }

    public static TsDataTable.DistributionType toDistributionType(DistributionType value) {
        return switch (value) {
            case DIST_FIRST -> TsDataTable.DistributionType.FIRST;
            case DIST_LAST -> TsDataTable.DistributionType.LAST;
            case DIST_MIDDLE -> TsDataTable.DistributionType.MIDDLE;
            default -> throw new IllegalArgumentException(value.name());
        };
    }

    public static ValueStatus fromValueStatus(TsDataTable.ValueStatus value) {
        return switch (value) {
            case PRESENT -> ValueStatus.VS_PRESENT;
            case UNUSED -> ValueStatus.VS_UNUSED;
            case BEFORE -> ValueStatus.VS_BEFORE;
            case AFTER -> ValueStatus.VS_AFTER;
            case EMPTY -> ValueStatus.VS_EMPTY;
        };
    }

    public static TsDataTable.ValueStatus toValueStatus(ValueStatus value) {
        return switch (value) {
            case VS_PRESENT -> TsDataTable.ValueStatus.PRESENT;
            case VS_UNUSED -> TsDataTable.ValueStatus.UNUSED;
            case VS_BEFORE -> TsDataTable.ValueStatus.BEFORE;
            case VS_AFTER -> TsDataTable.ValueStatus.AFTER;
            case VS_EMPTY -> TsDataTable.ValueStatus.EMPTY;
            default -> throw new IllegalArgumentException(value.name());
        };
    }

    public static MatrixDto fromMatrix(jdplus.toolkit.base.api.math.matrices.Matrix value) {
        MatrixDto.Builder result = MatrixDto
                .newBuilder()
                .setNrows(value.getRowsCount())
                .setNcols(value.getColumnsCount());
        Arrays.stream(value.toArray()).forEach(result::addValues);
        return result.build();
    }

    public static TemporalDisaggregationResultsDto fromTemporalDisaggregationResults(jdplus.benchmarking.base.core.univariate.TemporalDisaggregationResults value) {
        TemporalDisaggregationResultsDto.Builder result = TemporalDisaggregationResultsDto
                .newBuilder()
                .setOriginalSeries(fromTsData(value.getOriginalSeries()))
                .setDisaggregationDomain(fromTsDomain(value.getDisaggregationDomain()))
                .setHyperParametersCount(value.getHyperParametersCount())
                .setLikelihood(fromDiffuseConcentratedLikelihood(value.getLikelihood()))
                .setStats(fromDiffuseLikelihoodStatistics(value.getStats()))
//                .setResidualsDiagnostics(fromResidualsDiagnostics(value.getResidualsDiagnostics()))
                .setDisaggregatedSeries(fromTsData(value.getDisaggregatedSeries()))
                .setStDevDisaggregatedSeries(fromTsData(value.getStdevDisaggregatedSeries()));

        if (value.getMaximum() != null)
            result.setMaximum(fromObjectiveFunctionPoint(value.getMaximum()));

        if (value.getRegressionEffects() != null)
            result.setRegressionEffects(fromTsData(value.getRegressionEffects()));

        // TODO: indicators
//        Arrays.stream(value.getIndicators())
//                .forEach(indicator -> result.addIndicators(fromTsVariable(indicator)));
        System.out.println("Temporal disaggregation results: " + value);
        return result.build();
    }

    public static DiffuseConcentratedLikelihoodDto fromDiffuseConcentratedLikelihood(DiffuseConcentratedLikelihood value) {
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

    public static TsVariableDto fromTsVariable(TsVariable value) {
        TsVariableDto.Builder result = TsVariableDto
                .newBuilder()
                // TODO: .setName(?)
                .setId(value.getId())
                // TODO: .setLag(?)
                // TODO: .setCoefficient(?)
                ;
        return result.build();
    }

    public static ParameterDto fromParameter(Parameter value) {
        ParameterDto.Builder result = ParameterDto
                .newBuilder()
                .setValue(value.getValue())
                // TODO: .setDescription(value.toString())
                // It is set as parameter in R converter
                .setType(fromParameterType(value.getType()));
        return result.build();
    }

    public static DiffuseLikelihoodStatisticsDto fromDiffuseLikelihoodStatistics(DiffuseLikelihoodStatistics value) {
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

    public static TsDomainDto fromTsDomain(TsDomain value) {
        TsDomainDto.Builder result = TsDomainDto
                .newBuilder()
                .setStartPeriod(fromTsPeriod(value.getStartPeriod()))
                .setLength(value.getLength());

        return result.build();
    }

    public static ObjectiveFunctionPointDto fromObjectiveFunctionPoint(ObjectiveFunctionPoint value) {
        ObjectiveFunctionPointDto.Builder result = ObjectiveFunctionPointDto
                .newBuilder()
                .setValue(value.getValue())
                .setHessian(fromMatrix(value.getHessian()));
        Arrays.stream(value.getParameters()).forEach(result::addParameters);
        Arrays.stream(value.getGradient()).forEach(result::addGradient);

        return result.build();
    }

    public static ResidualsDiagnosticsDto fromResidualsDiagnostics(ResidualsDiagnostics value) {
        ResidualsDiagnosticsDto.Builder result = ResidualsDiagnosticsDto
                .newBuilder()
                .setFullResiduals(fromTsData(value.getFullResiduals()))
                .setNiid(fromNiidTests(value.getNiid()));

        return result.build();
    }

    public static NiidTestsDto fromNiidTests(NiidTests value) {
        NiidTestsDto.Builder result = NiidTestsDto
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

    public static StatisticalTestDto fromStatisticalTest(jdplus.toolkit.base.api.stats.StatisticalTest value) {
        StatisticalTestDto.Builder result = StatisticalTestDto
                .newBuilder()
                .setValue(value.getValue())
                .setPValue(value.getPvalue())
                .setDescription(value.getDescription());
        return result.build();
    }

    public static TramoSpec ToTramoSpec(TramoSpecDto dto) {
        return null;
    }
}
