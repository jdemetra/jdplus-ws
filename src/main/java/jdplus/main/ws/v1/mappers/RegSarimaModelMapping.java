package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.*;
import jdplus.toolkit.base.api.arima.SarimaSpec;
import jdplus.toolkit.base.api.data.DoubleSeq;
import jdplus.toolkit.base.api.data.Iterables;
import jdplus.toolkit.base.api.math.matrices.Matrix;
import jdplus.toolkit.base.api.stats.StatisticalTest;
import jdplus.toolkit.base.api.timeseries.TsDomain;
import jdplus.toolkit.base.api.timeseries.regression.*;
import jdplus.toolkit.base.core.modelling.GeneralLinearModel;
import jdplus.toolkit.base.core.regsarima.regular.RegSarimaModel;
import jdplus.toolkit.base.core.stats.likelihood.LikelihoodStatistics;

import static io.smallrye.openapi.model.DataType.type;

public class RegSarimaModelMapping {
    public static RegArimaModelDto toDto(RegSarimaModel model) {
        if (model == null) {
            return RegArimaModelDto.newBuilder().build();
        }

        DoubleSeq res = model.fullResiduals().getValues();
        return RegArimaModelDto.newBuilder()
                .setDescription(toDto(model.getDescription()))
                .setEstimation(toDto(model.getEstimation(),res))
                .setDiagnostics(toDiagnosticsDto(model))
                .build();
    }

    public static DiagnosticsDto toDiagnosticsDto(GeneralLinearModel<SarimaSpec> model) {
        DiagnosticsDto.Builder builder = DiagnosticsDto.newBuilder();
        model.getResiduals().getTests().forEach((k,v)->{
            if(v instanceof StatisticalTest st){
                if(st.isValid()){
                    StatisticalTestDto test = StatisticalTestMapping.toDto(st);
                    builder.putResidualsTests(k,test);
                }
            }
        });
        return builder.build();
    }

    public static RegArimaModelDto.DescriptionDto toDto(GeneralLinearModel.Description<SarimaSpec> description) {
        RegArimaModelDto.DescriptionDto.Builder builder = RegArimaModelDto.DescriptionDto.newBuilder();

        TsDomain domain = description.getSeries().getDomain();
        Variable[] vars = description.getVariables();
        for (int i = 0; i < vars.length; ++i) {
            Variable vari = vars[i];
            int m = vari.dim();
            ITsVariable core = vari.getCore();
            VariableType type = type(core);
            RegressionVariableDto.Builder vbuilder = RegressionVariableDto.newBuilder()
                    .setName(vari.getName())
                    .setVarType(type)
                    .putAllMetadata(vars[i].getAttributes());
            for (int k = 0; k < m; ++k) {
                String pname = m == 1 ? vari.getName() : vari.getCore().description(k, domain);
                vbuilder.addCoefficients(ParameterMapping.toDto(vari.getCoefficient(k), pname));
            }
            builder.addVariables(vbuilder.build());
        }

        return builder.setSeries(TsDataMapping.toDto(description.getSeries()))
                .setPreadjustment(EnumsMapping.toDto(description.getLengthOfPeriodTransformation()))
                .setLog(description.isLogTransformation())
                .setArima(SpecsMapping.toDto(description.getStochasticComponent()))
                .build();
    }

    public static RegArimaModelDto.EstimationDto toDto(GeneralLinearModel.Estimation estimation, DoubleSeq res) {
        RegArimaModelDto.EstimationDto.Builder builder = RegArimaModelDto.EstimationDto.newBuilder();

        Matrix cov = estimation.getCoefficientsCovariance();
        LikelihoodStatistics statistics = estimation.getStatistics();

        builder.addAllY(Iterables.of(estimation.getY()))
                .setX(MatrixMapping.toDto(estimation.getX()))
                .setParameters(ParametersEstimationMapping.toDto(estimation.getParameters()))
                .setLikelihood(LikelihoodStatisticsMapping.toDto(statistics))
                .addAllB(Iterables.of(estimation.getCoefficients()))
                .setBcovariance(MatrixMapping.toDto(cov))
                .addAllResiduals(Iterables.of(res));

        return builder.build();
    }

    public static VariableType type(ITsVariable var) {
        if (var instanceof TrendConstant) {
            return VariableType.VAR_MEAN;
        }
        if (var instanceof ITradingDaysVariable) {
            return VariableType.VAR_TD;
        }
        if (var instanceof ILengthOfPeriodVariable) {
            return VariableType.VAR_LP;
        }
        if (var instanceof IEasterVariable) {
            return VariableType.VAR_EASTER;
        }
        if (var instanceof IOutlier outlier) {
            switch (outlier.getCode()) {
                case AdditiveOutlier.CODE:
                    return VariableType.VAR_AO;
                case LevelShift.CODE:
                    return VariableType.VAR_LS;
                case TransitoryChange.CODE:
                    return VariableType.VAR_TC;
                case PeriodicOutlier.CODE:
                case PeriodicOutlier.PO:
                    return VariableType.VAR_SO;
                default:
                    return VariableType.VAR_OUTLIER;
            }
        }
        if (var instanceof InterventionVariable) {
            return VariableType.VAR_IV;
        }
        if (var instanceof Ramp) {
            return VariableType.VAR_RAMP;
        }
        return VariableType.VAR_UNSPECIFIED;
    }
}

