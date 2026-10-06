package jdplus.main.ws.v1;

import jdplus.benchmarking.base.core.univariate.ResidualsDiagnostics;
import jdplus.toolkit.base.api.arima.SarimaSpec;
import jdplus.toolkit.base.api.data.*;
import jdplus.toolkit.base.api.data.AggregationType;
import jdplus.toolkit.base.api.data.ParameterType;
import jdplus.toolkit.base.api.math.functions.ObjectiveFunctionPoint;
import jdplus.toolkit.base.api.modelling.TransformationType;
import jdplus.toolkit.base.api.timeseries.*;
import jdplus.toolkit.base.api.timeseries.calendars.LengthOfPeriodType;
import jdplus.toolkit.base.api.timeseries.calendars.TradingDaysType;
import jdplus.toolkit.base.api.timeseries.regression.*;
import jdplus.toolkit.base.api.timeseries.util.ObsGathering;
import jdplus.toolkit.base.core.stats.likelihood.DiffuseConcentratedLikelihood;
import jdplus.toolkit.base.core.stats.likelihood.DiffuseLikelihoodStatistics;
import jdplus.toolkit.base.core.stats.tests.Mean;
import jdplus.toolkit.base.core.stats.tests.NiidTests;
import jdplus.tramoseats.base.api.tramo.*;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
            case Last -> jdplus.main.ws.v1.AggregationType.AGGREGATION_LAST;
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
            case Initial -> jdplus.main.ws.v1.ParameterType.PARAMETER_INITIAL;
            case Fixed -> jdplus.main.ws.v1.ParameterType.PARAMETER_FIXED;
            case Estimated -> jdplus.main.ws.v1.ParameterType.PARAMETER_ESTIMATED;
            default -> jdplus.main.ws.v1.ParameterType.PARAMETER_UNUSED;
        };
    }

    public static ParameterType toParameterType(jdplus.main.ws.v1.ParameterType value) {
        return switch (value) {
            case PARAMETER_UNDEFINED -> ParameterType.Undefined;
            case PARAMETER_INITIAL -> ParameterType.Initial;
            case PARAMETER_FIXED -> ParameterType.Fixed;
            case PARAMETER_ESTIMATED -> ParameterType.Estimated;
            default -> null;
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

    public static Parameter toParameter(ParameterDto dto) {
        return Parameter.of(dto.getValue(), toParameterType(dto.getType()));
    }

    public static Parameter[] toParameter(List<ParameterDto> dtos) {
        return dtos.stream()
                .map(Converters::toParameter)
                .toArray(Parameter[]::new);
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
        return TramoSpec.builder()
                .frequency(dto.getBasic().getAnnualFrequency())
                .transform(toTransformSpec(dto.getBasic(), dto.getTransform()))
                .outliers(toOutlierSpec(dto.getOutlier()))
                .arima(toSarimaSpec(dto.getArima()))
                .autoModel(toAutoModelSpec(dto.getAutomodel())) // Check
                .regression(toRegressionSpec(dto.getRegression(),dto.getOutlier().getTcrate()))
                .estimate(toEstimateSpec(dto.getEstimate()))
                .buildWithoutValidation();
    }

    private static  EstimateSpec toEstimateSpec(EstimateSpecDto dto) {
        return EstimateSpec.builder()
                .span(toTimeSelector(dto.getSpan()))
                .tol(dto.getTol())
                .maximumLikelihood(dto.getMl())
                .ubp(dto.getUbp())
                .build();
    }

    public static TimeSelector.SelectionType toSelectionType(SelectionType value) {
        if (value == SelectionType.SPAN_ALL) {
            return TimeSelector.SelectionType.All;
        } else if (value == SelectionType.SPAN_FROM) {
            return TimeSelector.SelectionType.From;
        } else if (value == SelectionType.SPAN_TO) {
            return TimeSelector.SelectionType.To;
        } else if (value == SelectionType.SPAN_BETWEEN) {
            return TimeSelector.SelectionType.Between;
        } else if (value == SelectionType.SPAN_LAST) {
            return TimeSelector.SelectionType.Last;
        } else if (value == SelectionType.SPAN_FIRST) {
            return TimeSelector.SelectionType.First;
        } else if (value == SelectionType.SPAN_EXCLUDING) {
            return TimeSelector.SelectionType.Excluding;
        } else if (value == SelectionType.SPAN_NONE) {
            return TimeSelector.SelectionType.None;
        }

        return TimeSelector.SelectionType.None;
    }

    public static LocalDateTime toLocalDateTime(DateDto value) {
        return LocalDateTime.of(value.getYear(), value.getMonth(), value.getDay(), 0, 0, 0);
    }

    public static TimeSelector toTimeSelector(TimeSelectorDto dto) {
        return TimeSelector.builder().type(toSelectionType(dto.getType()))
                .n0(dto.getN0())
                .n1(dto.getN1())
                .d0(toLocalDateTime(dto.getD0()))
                .d1(toLocalDateTime(dto.getD1()))
                .build();
    }

    public static TransformationType toTransformationType(Transformation value) {
        return switch (value) {
            case FN_LOG -> TransformationType.Log;
            case FN_AUTO -> TransformationType.Auto;
            default -> TransformationType.None;
        };
    }

    public static LengthOfPeriodType toLengthOfPeriodType(LengthOfPeriod value) {
        return switch (value) {
            case LP_LEAPYEAR -> LengthOfPeriodType.LeapYear;
            case LP_LENGTHOFPERIOD -> LengthOfPeriodType.LengthOfPeriod;
            default -> LengthOfPeriodType.None;
        };
    }

    public static TransformSpec toTransformSpec(BasicSpecDto basicSpecDto, TransformSpecDto dto) {
        return TransformSpec.builder()
                .span(toTimeSelector( basicSpecDto.getSpan()))
                .preliminaryCheck(basicSpecDto.getPreliminaryCheck())
                .function(toTransformationType(dto.getTransformation()))
                .fct(dto.getFct())
                .adjust(toLengthOfPeriodType(dto.getAdjust()))
                .outliersCorrection(dto.getOutliersCorrection())
                .build();
    }

    public static OutlierSpec toOutlierSpec(OutlierSpecDto dto) {
        if (!dto.getEnabled()) {
            return OutlierSpec.DEFAULT_DISABLED;
        }

        return OutlierSpec.builder()
                .span(toTimeSelector(dto.getSpan()))
                .ao(dto.getAo())
                .ls(dto.getLs())
                .tc(dto.getTc())
                .so(dto.getSo())
                .criticalValue(dto.getVa())
                .deltaTC(dto.getTcrate())
                .maximumLikelihood(dto.getMl())
                .build();
    }

    public static SarimaSpec toSarimaSpec(SarimaSpecDto dto) {
        return SarimaSpec.builder()
                .period(dto.getPeriod())
                .phi(toParameter(dto.getPhiList()))
                .d(dto.getD())
                .theta(toParameter(dto.getThetaList()))
                .bphi(toParameter(dto.getBphiList()))
                .bd(dto.getBd())
                .btheta(toParameter(dto.getBthetaList()))
                .build();
    }

    public static AutoModelSpec toAutoModelSpec(AutoModelSpecDto dto) {
        return AutoModelSpec.builder()
                .enabled(dto.getEnabled())
                .cancel(dto.getCancel())
                .ub1(dto.getUb1())
                .ub2(dto.getUb2())
                .pcr(dto.getPcr())
                .pc(dto.getPc())
                .tsig(dto.getTsig())
                .acceptDefault(dto.getAcceptDef())
                .amiCompare(dto.getAmiCompare())
                .build();
    }

    public static RegressionSpec toRegressionSpec(RegressionSpecDto dto, double tc) {
        CalendarSpec.Builder cBuilder = CalendarSpec.builder();

        if (dto.hasEaster()) {
            cBuilder.easter(toEasterSpec(dto.getEaster()));
        }

        if (dto.hasTd()) {
            cBuilder.tradingDays(toTradingDaysSpec(dto.getTd()));
        }

        MeanSpec mean = MeanSpec.none();
        if (dto.hasMean()) {
            Parameter p = toParameter(dto.getMean());
            if (p != null) {
                boolean check = dto.getCheckMean();
                mean = MeanSpec.builder()
                        .trendConstant(true)
                        .test(check)
                        .coefficient(p)
                        .build();
            }
        }

        RegressionSpec.Builder builder = RegressionSpec.builder()
                .mean(mean)
                .calendar(cBuilder.build());

        int n = dto.getOutliersCount();
        for (int i = 0; i < n; ++i) {
            OutlierDto outlier = dto.getOutliers(i);
            builder.outlier(toVariable(outlier, tc));
        }

        n = dto.getUsersCount();
        for (int i = 0; i < n; ++i) {
            TsVariableDto var = dto.getUsers(i);
            builder.userDefinedVariable(toVariable(var));
        }

        n = dto.getInterventionsCount();
        for (int i = 0; i < n; ++i) {
            InterventionVariableDto var = dto.getInterventions(i);
            builder.interventionVariable(toVariable(var));
        }

        n = dto.getRampsCount();
        for (int i = 0; i < n; ++i) {
            RampDto var = dto.getRamps(i);
            builder.ramp(toVariable(var));
        }

        return builder
                .build();
    }

    public static EasterSpec toEasterSpec(EasterSpecDto dto) {
        var builder = EasterSpec.builder()
                .duration(dto.getDuration())
                .type(toEasterSpecType(dto.getType()))
                .test(dto.getTest())
                .julian(dto.getJulian());

        if (dto.hasCoefficient())
            builder.coefficient(toParameter(dto.getCoefficient()));

        return builder.build();
    }

    public static EasterSpec.Type toEasterSpecType(EasterType value) {
        return switch (value) {
            case EASTER_STANDARD -> EasterSpec.Type.Standard;
            case EASTER_INCLUDEEASTER -> EasterSpec.Type.IncludeEaster;
            case EASTER_INCLUDEEASTERMONDAY -> EasterSpec.Type.IncludeEasterMonday;
            default -> EasterSpec.Type.Unused;
        };
    }

    public static TradingDaysSpec toTradingDaysSpec(TradingDaysSpecDto dto) {
        String holidays = dto.getHolidays();
        TradingDaysType td = toTradingDaysType(dto.getTd());
        LengthOfPeriodType lp = toLengthOfPeriodType(dto.getLp());
        Parameter lpc = toParameter(dto.getLpcoefficient());
        Parameter[] tdc = toParameter(dto.getTdcoefficientsList());
        boolean test = isTest(dto);

        if (!holidays.isEmpty()) {
            TradingDaysSpec.AutoMethod auto = toAutoMethod(dto.getAuto());
            if (auto != TradingDaysSpec.AutoMethod.UNUSED) {
                return TradingDaysSpec.automaticHolidays(holidays, lp, auto, dto.getPtest(), dto.getAutoAdjust());
            }
            if (test) {
                return TradingDaysSpec.holidays(holidays, td, lp, toRegressionTestType(dto.getTest()), dto.getAutoAdjust());
            } else {
                return TradingDaysSpec.holidays(holidays, td, lp, tdc, lpc);
            }
        }

        int nusers = dto.getUsersCount();
        if (nusers > 0) {
            String[] users = new String[nusers];
            for (int i = 0; i < nusers; ++i) {
                users[i] = dto.getUsers(i);
            }
            if (test) {
                return TradingDaysSpec.userDefined(users, toRegressionTestType(dto.getTest()));
            } else {
                return TradingDaysSpec.userDefined(users, tdc);
            }
        }

        int w = dto.getW();
        if (w > 0) {
            return TradingDaysSpec.stockTradingDays(w, toRegressionTestType(dto.getTest()));
        }

        TradingDaysSpec.AutoMethod auto = toAutoMethod(dto.getAuto());
        if (auto != TradingDaysSpec.AutoMethod.UNUSED) {
            return TradingDaysSpec.automatic(lp, auto, dto.getPtest(), dto.getAutoAdjust());
        } else if (td == TradingDaysType.NONE) {
            return TradingDaysSpec.none();
        } else {
            if (test) {
                return TradingDaysSpec.td(td, lp, toRegressionTestType(dto.getTest()), dto.getAutoAdjust());
            } else {
                return TradingDaysSpec.td(td, lp, tdc, lpc);
            }
        }
    }

    private static boolean isTest(TradingDaysSpecDto dto) {
        return dto.getAuto() != AutomaticTradingDays.TD_AUTO_NO
                || dto.getTest() == TradingDaysTest.TD_TEST_JOINT_F
                || dto.getTest() == TradingDaysTest.TD_TEST_SEPARATE_T;
    }

    public static TradingDaysType toTradingDaysType(TradingDays value) {
        return switch (value) {
            case TD7 -> TradingDaysType.TD7;
            case TD4 -> TradingDaysType.TD4;
            case TD3 -> TradingDaysType.TD3;
            case TD3C -> TradingDaysType.TD3c;
            case TD2C -> TradingDaysType.TD2c;
            case TD2 -> TradingDaysType.TD2;
            default -> TradingDaysType.NONE;
        };
    }

    public static TradingDaysSpec.AutoMethod toAutoMethod(AutomaticTradingDays value) {
        return switch (value) {
            case TD_AUTO_FTEST -> TradingDaysSpec.AutoMethod.FTEST;
            case TD_AUTO_WALD -> TradingDaysSpec.AutoMethod.WALD;
            case TD_AUTO_AIC -> TradingDaysSpec.AutoMethod.AIC;
            case TD_AUTO_BIC -> TradingDaysSpec.AutoMethod.BIC;
            default -> TradingDaysSpec.AutoMethod.UNUSED;
        };
    }

    public static RegressionTestType toRegressionTestType(TradingDaysTest value) {
        return switch (value) {
            case TD_TEST_JOINT_F -> RegressionTestType.Joint_F;
            case TD_TEST_SEPARATE_T -> RegressionTestType.Separate_T;
            default -> RegressionTestType.None;
        };
    }

    public static Variable<IOutlier> toVariable(OutlierDto outlier, double tc) {
        LocalDate ldt = toLocalDate(outlier.getPosition());
        IOutlier o;
        switch (outlier.getCode()) {
            case "ao":
            case "AO":
                o = new AdditiveOutlier(ldt.atStartOfDay());
                break;
            case "ls":
            case "LS":
                o = new LevelShift(ldt.atStartOfDay(), true);
                break;
            case "tc":
            case "TC":
                o = new TransitoryChange(ldt.atStartOfDay(), tc);
                break;
            case "so":
            case "SO":
                o = new PeriodicOutlier(ldt.atStartOfDay(), 0, true);
                break;

            default:
                return null;
        }
        Parameter c = toParameter(outlier.getCoefficient());
        return Variable.<IOutlier>builder()
                .core(o)
                .name(outlier.getName())
                .coefficients(c == null ? null : new Parameter[]{c})
                .attributes(outlier.getMetadataMap())
                .build();
    }

    public static Variable<TsContextVariable> toVariable(TsVariableDto v) {
        Parameter c = toParameter(v.getCoefficient());
        return Variable.<TsContextVariable>builder()
                .name(v.getName())
                .core(new TsContextVariable(v.getId(), v.getLag()))
                .attributes(v.getMetadataMap())
                .coefficients(c == null ? null : new Parameter[]{c})
                .build();
    }

    public static Variable<Ramp> toVariable(RampDto v) {
        LocalDate start = toLocalDate(v.getStart());
        LocalDate end = toLocalDate(v.getEnd());
        Parameter c = toParameter(v.getCoefficient());
        return Variable.<Ramp>builder()
                .name(v.getName())
                .core(new Ramp(start.atStartOfDay(), end.atStartOfDay()))
                .attributes(v.getMetadataMap())
                .coefficients(c == null ? null : new Parameter[]{c})
                .build();
    }

    public static Variable<InterventionVariable> toVariable(InterventionVariableDto v) {
        InterventionVariable.Builder builder = InterventionVariable.builder()
                .delta(v.getDelta())
                .deltaSeasonal(v.getSeasonalDelta());
        int n = v.getSequencesCount();
        for (int i = 0; i < n; ++i) {
           InterventionVariableDto.SequenceDto seq = v.getSequences(i);
            LocalDate start = toLocalDate(seq.getStart());
            LocalDate end = toLocalDate(seq.getEnd());
            builder.sequence(Range.of(start.atStartOfDay(), end.atStartOfDay()));
        }
        Parameter c = toParameter(v.getCoefficient());
        return Variable.<InterventionVariable>builder()
                .name(v.getName())
                .core(builder.build())
                .coefficients(c == null ? null : new Parameter[]{c})
                .attributes(v.getMetadataMap())
                .build();
    }
}
