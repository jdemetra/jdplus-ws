package jdplus.main.ws.v1;

import java.time.LocalDate;
import java.util.Arrays;
import jdplus.benchmarking.base.api.univariate.*;
import jdplus.benchmarking.base.core.univariate.TemporalDisaggregationProcessor;
import jdplus.benchmarking.base.core.univariate.TemporalDisaggregationResults;
import jdplus.main.ws.v1.mappers.*;
import jdplus.toolkit.base.api.data.Parameter;
import jdplus.toolkit.base.api.math.matrices.Matrix;
import jdplus.toolkit.base.api.ssf.SsfInitialization;
import jdplus.toolkit.base.api.timeseries.TsData;
import jdplus.toolkit.base.api.timeseries.TsDataTable;
import jdplus.toolkit.base.api.timeseries.util.TsDataBuilder;
import jdplus.toolkit.base.api.util.Table;
import jdplus.toolkit.base.core.arima.ArimaModel;
import jdplus.toolkit.base.core.math.linearfilters.BackFilter;
import jdplus.toolkit.base.core.math.matrices.FastMatrix;
import jdplus.toolkit.base.core.regsarima.regular.Forecast;
import jdplus.toolkit.base.core.regsarima.regular.RegSarimaModel;
import jdplus.toolkit.base.core.ssf.StateComponent;
import jdplus.toolkit.base.core.ssf.arima.SsfArima;
import jdplus.toolkit.base.core.ssf.composite.CompositeSsf;
import jdplus.toolkit.base.core.ssf.dk.DkToolkit;
import jdplus.toolkit.base.core.ssf.sts.Noise;
import jdplus.toolkit.base.core.ssf.univariate.DefaultSmoothingResults;
import jdplus.toolkit.base.core.ssf.univariate.SsfData;
import jdplus.toolkit.base.core.stats.DescriptiveStatistics;
import jdplus.tramoseats.base.api.tramo.TramoSpec;
import jdplus.tramoseats.base.core.tramo.TramoFactory;
import jdplus.tramoseats.base.core.tramo.TramoKernel;
import jdplus.tramoseats.base.core.tramo.TramoOutput;

class Helpers {

    static ResultStatusDto ok() {
        return ResultStatusDto.newBuilder()
                .setType(ResultStatusType.STATUS_OK)
                .setMessage("")
                .build();
    }

    static ResultStatusDto ko(String message) {
        return ResultStatusDto.newBuilder()
                .setType(ResultStatusType.STATUS_ERROR)
                .setMessage(message)
                .build();
    }

    static TsFunctionResponseDto normalize(TsFunctionRequestDto input) {
        TsFunctionResponseDto.Builder builder =
                TsFunctionResponseDto.newBuilder().setId(input.getId());

        try {
            TsFunctionOutputDto result = TsFunctionOutputDto.newBuilder()
                    .setStatus(ok())
                    .setSeries(TsDataMapping.toDto(
                            (input.hasSeries() ? TsDataMapping.toModel(input.getSeries()) : TsData.empty("?"))
                                    .normalize()))
                    .build();

            builder.setOk(result);
        } catch (Exception e) {
            builder.setErr(ErrorDto.newBuilder().setMessage(e.getMessage()).build());
        }

        return builder.build();
    }

    static DescriptiveStatisticsResponseDto statistics(TsFunctionRequestDto input) {
        DescriptiveStatisticsResponseDto.Builder builder =
                DescriptiveStatisticsResponseDto.newBuilder().setId(input.getId());

        try {
            DescriptiveStatistics value = DescriptiveStatistics.of(
                    TsDataMapping.toModel(input.getSeries()).getValues());
            double[] quantiles = value.quantiles(4);

            builder.setOk(DescriptiveStatisticsDto.newBuilder()
                    .setStatus(ok())
                    .setN(value.getDataCount())
                    .setNmissing(value.getMissingValuesCount())
                    .setMax(value.getMax())
                    .setMin(value.getMin())
                    .setAverage(value.getAverage())
                    .setStdev(value.getStdev())
                    .setQ25(quantiles[0])
                    .setQ50(quantiles[1])
                    .setQ75(quantiles[2])
                    .build());
        } catch (Exception e) {
            builder.setErr(ErrorDto.newBuilder().setMessage(e.getMessage()).build());
        }

        return builder.build();
    }

    static TsFunctionResponseDto pct(PctRequestDto input) {
        TsFunctionResponseDto.Builder builder =
                TsFunctionResponseDto.newBuilder().setId(input.getId());

        try {
            builder.setOk(TsFunctionOutputDto.newBuilder()
                    .setStatus(ok())
                    .setSeries(TsDataMapping.toDto(
                            TsDataMapping.toModel(input.getSeries()).pctVariation(input.getLag())))
                    .build());
        } catch (Exception e) {
            builder.setErr(ErrorDto.newBuilder().setMessage(e.getMessage()).build());
        }

        return builder.build();
    }

    static TsFunctionResponseDto delta(DeltaRequestDto input) {
        TsFunctionResponseDto.Builder builder =
                TsFunctionResponseDto.newBuilder().setId(input.getId());

        try {
            builder.setOk(TsFunctionOutputDto.newBuilder()
                    .setStatus(ok())
                    .setSeries(TsDataMapping.toDto(
                            TsDataMapping.toModel(input.getSeries()).delta(input.getLag(), input.getPower())))
                    .build());
        } catch (Exception e) {
            builder.setErr(ErrorDto.newBuilder().setMessage(e.getMessage()).build());
        }

        return builder.build();
    }

    static TsFunctionResponseDto aggregate(AggregationRequestDto input) {
        TsFunctionResponseDto.Builder builder =
                TsFunctionResponseDto.newBuilder().setId(input.getId());

        try {
            TsData result = TsDataMapping.toModel(input.getSeries())
                    .aggregate(
                            EnumsMapping.toModel(input.getNewFrequency()),
                            EnumsMapping.toModel(input.getAggregationType()),
                            input.getComplete());

            builder.setOk(TsFunctionOutputDto.newBuilder()
                    .setStatus(ok())
                    .setSeries(TsDataMapping.toDto(result))
                    .build());
        } catch (Exception e) {
            builder.setErr(ErrorDto.newBuilder().setMessage(e.getMessage()).build());
        }

        return builder.build();
    }

    static HodrickPrescottResponseDto hodrickPrescott(HodrickPrescottRequestDto input) {
        HodrickPrescottResponseDto.Builder builder =
                HodrickPrescottResponseDto.newBuilder().setId(input.getId());

        try {
            TsData s = TsDataMapping.toModel(input.getSeries());

            ArimaModel rw2 = new ArimaModel(BackFilter.ONE, BackFilter.ofInternal(1, -2, 1), BackFilter.ONE, 1);
            StateComponent signal = SsfArima.stateComponent(rw2);
            StateComponent n = Noise.of(input.getLambda());

            // create a composite state space form
            CompositeSsf ssf = CompositeSsf.builder()
                    .add(signal, SsfArima.defaultLoading())
                    .add(n, Noise.defaultLoading())
                    .build();

            // smoothing using Durbin-Koopman for diffuse initialization
            // and with the specified variances (not estimated)
            SsfData data = new SsfData(s.getValues());
            DefaultSmoothingResults rslts = DkToolkit.sqrtSmooth(ssf, data, true, true);
            int[] pos = ssf.componentsPosition();

            TsData trend = TsData.of(s.getStart(), rslts.getComponent(pos[0]));
            TsData noise = TsData.of(s.getStart(), rslts.getComponent(pos[1]));

            builder.setOk(HodrickPrescottOutputDto.newBuilder()
                    .setStatus(ok())
                    .setTrend(TsDataMapping.toDto(trend))
                    .setNoise(TsDataMapping.toDto(noise))
                    .build());
        } catch (Exception e) {
            builder.setErr(ErrorDto.newBuilder().setMessage(e.getMessage()).build());
        }

        return builder.build();
    }

    static BuildTsDataResponseDto buildTsData(BuildTsDataRequestDto request) {
        BuildTsDataResponseDto.Builder responseBuilder =
                BuildTsDataResponseDto.newBuilder().setId(request.getId());

        try {
            TsDataBuilder<LocalDate> builder =
                    TsDataBuilder.byDate(ObsGatheringMapping.toModel(request.getGathering()));
            request.getObservationsList()
                    .forEach(obs -> builder.add(LocalDateMapping.toModel(obs.getDate()), obs.getValue()));
            TsData result = builder.build();

            responseBuilder.setOk(TsFunctionOutputDto.newBuilder()
                    .setStatus(result.isEmpty() ? ko(result.getEmptyCause()) : ok())
                    .setSeries(TsDataMapping.toDto(result))
                    .build());
        } catch (Exception e) {
            responseBuilder.setErr(
                    ErrorDto.newBuilder().setMessage(e.getMessage()).build());
        }

        return responseBuilder.build();
    }

    static BuildTsDataTableResponseDto buildTsDataTable(BuildTsDataTableRequestDto request) {
        BuildTsDataTableResponseDto.Builder builder =
                BuildTsDataTableResponseDto.newBuilder().setId(request.getId());

        try {
            TsDataTable result = TsDataTable.of(request.getCollectionList(), TsDataMapping::toModel);

            TsDataTable.Cursor cursor = result.cursor(EnumsMapping.toModel(request.getDistributionType()));
            Matrix.Mutable matrix = Matrix.Mutable.make(cursor.getPeriodCount(), cursor.getSeriesCount());
            Table<ValueStatus> statuses = new Table<>(cursor.getPeriodCount(), cursor.getSeriesCount());
            for (int i = 0; i < cursor.getPeriodCount(); i++) {
                for (int j = 0; j < cursor.getSeriesCount(); j++) {
                    cursor.moveTo(i, j);
                    statuses.set(i, j, EnumsMapping.toDto(cursor.getStatus()));
                    matrix.set(i, j, cursor.getValue());
                }
            }

            TsMatrixDto tsMatrix = TsMatrixDto.newBuilder()
                    .setStart(TsPeriodMapping.toDto(result.getDomain().getStartPeriod()))
                    .setValues(MatrixMapping.toDto(matrix))
                    .build();

            ValueStatus[] buffer = new ValueStatus[statuses.getRowsCount() * statuses.getColumnsCount()];
            statuses.copyTo(buffer);

            builder.setOk(BuildTsDataTableOutputDto.newBuilder()
                    .setMatrix(tsMatrix)
                    .addAllStatuses(Arrays.asList(buffer))
                    .build());
        } catch (Exception e) {
            builder.setErr(ErrorDto.newBuilder().setMessage(e.getMessage()).build());
        }

        return builder.build();
    }

    static TemporalDisaggregationResponseDto processTemporalDisaggregation(TemporalDisaggregationRequestDto request) {
        TemporalDisaggregationResponseDto.Builder builder =
                TemporalDisaggregationResponseDto.newBuilder().setId(request.getId());

        try {
            TsData y = TsDataMapping.toModel(request.getY());
            TsData[] indicators = request.getIndicatorsList().stream()
                    .map(TsDataMapping::toModel)
                    .toArray(TsData[]::new);
            boolean constant = request.getConstant();
            boolean trend = request.getTrend();
            String model = request.getModel();
            boolean average = request.getAverage();
            double rho = request.getRho();
            boolean fixedRho = request.getFixedRho();
            double truncatedRho = request.getTruncatedRho();
            boolean zeroInit = request.getZeroInit();
            String algorithm = request.getAlgorithm();
            boolean diffuserEgs = request.getDiffuserEgs();
            int nbackcasts = request.hasNBackcasts() ? request.getNBackcasts() : 0;
            int nforecasts = request.hasNForecasts() ? request.getNForecasts() : 0;
            int frequency = request.hasFrequency() ? request.getFrequency() : 12;

            ModelSpec mspec = ModelSpec.builder()
                    .constant(constant)
                    .trend(trend)
                    .residualsModel(ResidualsModel.valueOf(model))
                    .parameter(fixedRho ? Parameter.fixed(rho) : Parameter.initial(rho))
                    .diffuseRegressors(diffuserEgs)
                    .zeroInitialization(zeroInit)
                    .build();

            TsEstimationSpec espec = TsEstimationSpec.builder()
                    .truncatedParameter(truncatedRho <= -1 ? null : truncatedRho)
                    .build();

            AlgorithmSpec aspec = AlgorithmSpec.builder()
                    .algorithm(SsfInitialization.valueOf(algorithm))
                    .rescale(true)
                    .build();

            if (indicators.length > 0) {
                TemporalDisaggregationSpec spec = TemporalDisaggregationSpec.builder()
                        .modelSpec(mspec)
                        .estimationSpec(espec)
                        .algorithmSpec(aspec)
                        .average(average)
                        .build();

                for (int i = 0; i < indicators.length; ++i) {
                    indicators[i] = indicators[i].cleanExtremities();
                }
                TemporalDisaggregationResults results = TemporalDisaggregationProcessor.process(y, indicators, spec);
                builder.setOk(TemporalDisaggregationMapping.toDto(results));
            } else {
                TemporalDisaggregationSpec spec = TemporalDisaggregationSpec.builder()
                        .modelSpec(mspec)
                        .estimationSpec(espec)
                        .algorithmSpec(aspec)
                        .average(average)
                        .defaultPeriod(frequency)
                        .build();

                TemporalDisaggregationResults results =
                        TemporalDisaggregationProcessor.process(y, nbackcasts, nforecasts, spec);

                builder.setOk(TemporalDisaggregationMapping.toDto(results));
            }

        } catch (Exception e) {
            builder.setErr(ErrorDto.newBuilder().setMessage(e.getMessage()).build());
        }

        return builder.build();
    }

    /**
     * Get TramoSpec from request, with simplified specs or full ones.
     */
    private static TramoSpec getSpecFromRequest(TramoRequestDto request) {
        return switch (request.getSpecCase()) {
            case DEFSPEC -> TramoSpec.fromString(request.getDefSpec());
            case FULLSPEC -> SpecsMapping.toModel(request.getFullSpec());
            case SPEC_NOT_SET -> TramoSpec.fromString("TRfull");
        };
    }

    public static TramoForecastResponseDto tramoForecast(TramoRequestDto request) {
        TramoForecastResponseDto.Builder builder =
                TramoForecastResponseDto.newBuilder().setId(request.getId());

        try {
            TsData series = TsDataMapping.toModel(request.getSeries());
            int nForecasts = request.getNForecasts();
            TramoSpec spec = getSpecFromRequest(request);
            TramoKernel kernel;
            if (request.hasModellingContext()) {
                kernel = TramoKernel.of(spec, ModellingContextMapping.toModel(request.getModellingContext()));
            } else {
                kernel = TramoKernel.of(spec, null);
            }

            Forecast f = new Forecast(kernel, nForecasts);
            if (!f.process(series.cleanExtremities())) {
                throw new RuntimeException("Tramo forecast processing failed");
            }

            FastMatrix R = FastMatrix.make(nForecasts, 4);
            R.column(0).copy(f.getForecasts());
            R.column(1).copy(f.getForecastsStdev());
            R.column(2).copy(f.getRawForecasts());
            R.column(3).copy(f.getRawForecastsStdev());

            builder.setOk(TsMatrixDto.newBuilder()
                    .setStart(TsPeriodMapping.toDto(series.getEnd()))
                    .setValues(MatrixMapping.toDto(R))
                    .build());
        } catch (Exception e) {
            builder.setErr(ErrorDto.newBuilder().setMessage(e.getMessage()).build());
        }

        return builder.build();
    }

    public static TramoFullProcessResponseDto tramoFullProcess(TramoRequestDto request) {
        TramoFullProcessResponseDto.Builder builder =
                TramoFullProcessResponseDto.newBuilder().setId(request.getId());

        try {
            TsData series = TsDataMapping.toModel(request.getSeries());
            TramoSpec spec = getSpecFromRequest(request);
            TramoKernel kernel;
            if (request.hasModellingContext()) {
                kernel = TramoKernel.of(spec, ModellingContextMapping.toModel(request.getModellingContext()));
            } else {
                kernel = TramoKernel.of(spec, null);
            }
            RegSarimaModel estimation = kernel.process(series.cleanExtremities(), null);
            TramoOutput tramoOutput = TramoOutput.builder()
                    .estimationSpec(spec)
                    .result(estimation)
                    .resultSpec(
                            estimation == null
                                    ? null
                                    : TramoFactory.getInstance().generateSpec(spec, estimation.getDescription()))
                    .build();

            builder.setOk(TramoOutputMapping.toDto(tramoOutput));
        } catch (Exception e) {
            builder.setErr(ErrorDto.newBuilder().setMessage(e.getMessage()).build());
        }

        return builder.build();
    }
}
