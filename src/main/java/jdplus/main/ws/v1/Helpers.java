package jdplus.main.ws.v1;

import jdplus.benchmarking.base.api.univariate.*;
import jdplus.benchmarking.base.core.univariate.TemporalDisaggregationProcessor;
import jdplus.benchmarking.base.core.univariate.TemporalDisaggregationResults;
import jdplus.toolkit.base.api.data.Parameter;
import jdplus.toolkit.base.api.math.matrices.Matrix;
import jdplus.toolkit.base.api.ssf.SsfInitialization;
import jdplus.toolkit.base.api.timeseries.TsData;
import jdplus.toolkit.base.api.timeseries.TsDataTable;
import jdplus.toolkit.base.api.timeseries.util.TsDataBuilder;
import jdplus.toolkit.base.api.util.Table;
import jdplus.toolkit.base.core.arima.ArimaModel;
import jdplus.toolkit.base.core.math.linearfilters.BackFilter;
import jdplus.toolkit.base.core.ssf.StateComponent;
import jdplus.toolkit.base.core.ssf.arima.SsfArima;
import jdplus.toolkit.base.core.ssf.composite.CompositeSsf;
import jdplus.toolkit.base.core.ssf.dk.DkToolkit;
import jdplus.toolkit.base.core.ssf.sts.Noise;
import jdplus.toolkit.base.core.ssf.univariate.DefaultSmoothingResults;
import jdplus.toolkit.base.core.ssf.univariate.SsfData;
import jdplus.toolkit.base.core.stats.DescriptiveStatistics;

import java.time.LocalDate;
import java.util.Arrays;

import static jdplus.main.ws.v1.Converters.*;

class Helpers {

    static ToolkitMessages.ResultStatusDto ok() {
        return ToolkitMessages.ResultStatusDto
                .newBuilder()
                .setType(ToolkitMessages.ResultStatusType.STATUS_OK)
                .setMessage("")
                .build();
    }

    static ToolkitMessages.ResultStatusDto ko(String message) {
        return ToolkitMessages.ResultStatusDto
                .newBuilder()
                .setType(ToolkitMessages.ResultStatusType.STATUS_ERROR)
                .setMessage(message)
                .build();
    }

    static ToolkitMessages.TsFunctionOutputDto normalize(ToolkitMessages.TsFunctionInputDto input) {
        return ToolkitMessages.TsFunctionOutputDto
                .newBuilder()
                .setId(input.getId())
                .setStatus(ok())
                .setSeries(fromTsData((input.hasSeries() ? toTsData(input.getSeries()) : TsData.empty("?")).normalize()))
                .build();
    }

    static ToolkitMessages.DescriptiveStatisticsDto statistics(ToolkitMessages.TsFunctionInputDto input) {
        DescriptiveStatistics value = DescriptiveStatistics.of(toTsData(input.getSeries()).getValues());
        double[] quantiles = value.quantiles(4);
        return ToolkitMessages.DescriptiveStatisticsDto
                .newBuilder()
                .setId(input.getId())
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
                .build();
    }

    static ToolkitMessages.TsFunctionOutputDto pct(ToolkitMessages.PctInputDto input) {
        return ToolkitMessages.TsFunctionOutputDto
                .newBuilder()
                .setId(input.getId())
                .setStatus(ok())
                .setSeries(fromTsData(toTsData(input.getSeries()).pctVariation(input.getLag())))
                .build();
    }

    static ToolkitMessages.TsFunctionOutputDto delta(ToolkitMessages.DeltaInputDto input) {
        return ToolkitMessages.TsFunctionOutputDto
                .newBuilder()
                .setId(input.getId())
                .setStatus(ok())
                .setSeries(fromTsData(toTsData(input.getSeries()).delta(input.getLag(), input.getPower())))
                .build();
    }

    static ToolkitMessages.TsFunctionOutputDto aggregate(ToolkitMessages.AggregationInputDto input) {
        TsData result = toTsData(input.getSeries()).aggregate(toTsUnit(input.getNewFrequency()), toAggregationType(input.getAggregationType()), input.getComplete());
        return ToolkitMessages.TsFunctionOutputDto
                .newBuilder()
                .setId(input.getId())
                .setStatus(ok())
                .setSeries(fromTsData(result))
                .build();
    }

    static ToolkitMessages.HodrickPrescottOutputDto hodrickPrescott(ToolkitMessages.HodrickPrescottInputDto input) {
        TsData s = toTsData(input.getSeries());

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

        return ToolkitMessages.HodrickPrescottOutputDto
                .newBuilder()
                .setId(input.getId())
                .setStatus(ok())
                .setTrend(fromTsData(trend))
                .setNoise(fromTsData(noise))
                .build();
    }

    static ToolkitMessages.TsFunctionOutputDto buildTsData(ToolkitMessages.BuildTsDataInputDto request) {
        TsDataBuilder<LocalDate> builder = TsDataBuilder.byDate(toObsGathering(request.getGathering()));
        request.getObservationsList().forEach(obs -> builder.add(toLocalDate(obs.getDate()), obs.getValue()));
        TsData result = builder.build();

        return ToolkitMessages.TsFunctionOutputDto
                .newBuilder()
                .setId(request.getId())
                .setStatus(result.isEmpty() ? ko(result.getEmptyCause()) : ok())
                .setSeries(fromTsData(result))
                .build();
    }

    static ToolkitMessages.BuildTsDataTableOutputDto buildTsDataTable(ToolkitMessages.BuildTsDataTableInputDto request) {
        TsDataTable result = TsDataTable.of(request.getCollectionList(), Converters::toTsData);

        TsDataTable.Cursor cursor = result.cursor(toDistributionType(request.getDistributionType()));
        Matrix.Mutable matrix = Matrix.Mutable.make(cursor.getPeriodCount(), cursor.getSeriesCount());
        Table<ToolkitMessages.ValueStatus> statuses = new Table<>(cursor.getPeriodCount(), cursor.getSeriesCount());
        for (int i = 0; i < cursor.getPeriodCount(); i++) {
            for (int j = 0; j < cursor.getSeriesCount(); j++) {
                cursor.moveTo(i, j);
                statuses.set(i, j, Converters.fromValueStatus(cursor.getStatus()));
                matrix.set(i, j, cursor.getValue());
            }
        }

        ToolkitMessages.TsMatrixDto tsMatrix = ToolkitMessages.TsMatrixDto
                .newBuilder()
                .setStart(fromTsPeriod(result.getDomain().getStartPeriod()))
                .setValues(fromMatrix(matrix))
                .build();

        ToolkitMessages.ValueStatus[] buffer = new ToolkitMessages.ValueStatus[statuses.getRowsCount() * statuses.getColumnsCount()];
        statuses.copyTo(buffer);

        return ToolkitMessages.BuildTsDataTableOutputDto
                .newBuilder()
                .setId(request.getId())
                .setMatrix(tsMatrix)
                .addAllStatuses(Arrays.asList(buffer))
                .build();
    }

    static ToolkitMessages.TemporalDisaggregationResultsDto processTemporalDisaggregation(ToolkitMessages.TemporalDisaggregationRequestDto request) {
        TsData y = Converters.toTsData(request.getY());
        TsData[] indicators =  request.getIndicatorsList().stream().map(Converters::toTsData).toArray(TsData[]::new);
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

        TemporalDisaggregationSpec spec = TemporalDisaggregationSpec.builder()
                .modelSpec(mspec)
                .estimationSpec(espec)
                .algorithmSpec(aspec)
                .average(average)
                .build();

        TemporalDisaggregationResults results;
        if ( indicators.length > 0) {
            for (int i = 0; i < indicators.length; ++i) {
                indicators[i] = indicators[i].cleanExtremities();
            }
            results = TemporalDisaggregationProcessor.process(y, indicators, spec);
        }
        else {
            results = TemporalDisaggregationProcessor.process(y, nbackcasts, nforecasts, spec);
        }

        return Converters.fromTemporalDisaggregationResults(results);
    }
}
