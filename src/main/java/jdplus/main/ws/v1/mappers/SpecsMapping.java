package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.*;
import jdplus.toolkit.base.api.arima.SarimaSpec;
import jdplus.toolkit.base.api.data.Parameter;
import jdplus.toolkit.base.api.timeseries.calendars.LengthOfPeriodType;
import jdplus.toolkit.base.api.timeseries.calendars.TradingDaysType;
import jdplus.toolkit.base.api.timeseries.regression.*;
import jdplus.tramoseats.base.api.tramo.*;

import java.util.List;

public class SpecsMapping {
    public static TramoSpec toModel(TramoSpecDto dto) {
        return TramoSpec.builder()
                .frequency(dto.getBasic().getAnnualFrequency())
                .transform(toModel(dto.getBasic(), dto.getTransform()))
                .outliers(toModel(dto.getOutlier()))
                .arima(toModel(dto.getArima()))
                .autoModel(toModel(dto.getAutomodel()))
                .regression(toModel(dto.getRegression(), dto.getOutlier().getTcrate()))
                .estimate(toModel(dto.getEstimate()))
                .buildWithoutValidation();
    }

    public static TramoSpecDto toDto(TramoSpec spec) {
        return TramoSpecDto.newBuilder()
                .setBasic(toDto(spec.getTransform(), spec.getFrequency()))
                .setTransform(toDto(spec.getTransform()))
                .setOutlier(toDto(spec.getOutliers()))
                .setArima(toDto(spec.getArima()))
                .setAutomodel(toDto(spec.getAutoModel()))
                .setRegression(toDto(spec.getRegression()))
                .setEstimate(toDto(spec.getEstimate()))
                .build();
    }

    private static BasicSpecDto toDto(TransformSpec transformSpec, int freq) {
        return BasicSpecDto.newBuilder()
                .setSpan(TimeSelectorMapping.toDto(transformSpec.getSpan()))
                .setPreliminaryCheck(transformSpec.isPreliminaryCheck())
                .setAnnualFrequency(freq)
                .build();
    }

    private static EstimateSpec toModel(EstimateSpecDto dto) {
        return EstimateSpec.builder()
                .span(TimeSelectorMapping.toModel(dto.getSpan()))
                .tol(dto.getTol())
                .maximumLikelihood(dto.getMl())
                .ubp(dto.getUbp())
                .build();
    }

    private static EstimateSpecDto toDto(EstimateSpec spec) {
        EstimateSpecDto.Builder builder = EstimateSpecDto.newBuilder();

        builder.setSpan(TimeSelectorMapping.toDto(spec.getSpan()))
                .setTol(spec.getTol())
                .setMl(spec.isMaximumLikelihood())
                .setUbp(spec.getUbp());

        return builder.build();
    }

    public static TransformSpec toModel(BasicSpecDto basicSpecDto, TransformSpecDto dto) {
        return TransformSpec.builder()
                .span(TimeSelectorMapping.toModel(basicSpecDto.getSpan()))
                .preliminaryCheck(basicSpecDto.getPreliminaryCheck())
                .function(EnumsMapping.toModel(dto.getTransformation()))
                .fct(dto.getFct())
                .adjust(EnumsMapping.toModel(dto.getAdjust()))
                .outliersCorrection(dto.getOutliersCorrection())
                .build();
    }

    public static TransformSpecDto toDto(TransformSpec spec) {
        TransformSpecDto.Builder builder = TransformSpecDto.newBuilder();

        builder.setTransformation(EnumsMapping.toDto(spec.getFunction()))
                .setFct(spec.getFct())
                .setAdjust(EnumsMapping.toDto(spec.getAdjust()))
                .setOutliersCorrection(spec.isOutliersCorrection());

        return builder.build();
    }

    public static OutlierSpec toModel(OutlierSpecDto dto) {
        if (!dto.getEnabled()) {
            return OutlierSpec.DEFAULT_DISABLED;
        }

        return OutlierSpec.builder()
                .span(TimeSelectorMapping.toModel(dto.getSpan()))
                .ao(dto.getAo())
                .ls(dto.getLs())
                .tc(dto.getTc())
                .so(dto.getSo())
                .criticalValue(dto.getVa())
                .deltaTC(dto.getTcrate())
                .maximumLikelihood(dto.getMl())
                .build();
    }

    public static OutlierSpecDto toDto(OutlierSpec spec) {
        OutlierSpecDto.Builder builder = OutlierSpecDto.newBuilder();

        builder.setSpan(TimeSelectorMapping.toDto(spec.getSpan()))
                .setEnabled(spec.isUsed())
                .setVa(spec.getCriticalValue())
                .setTcrate(spec.getDeltaTC())
                .setAo(spec.isAo())
                .setLs(spec.isLs())
                .setTc(spec.isTc())
                .setSo(spec.isSo())
                .setMl(spec.isMaximumLikelihood());

        return builder.build();
    }

    public static SarimaSpec toModel(SarimaSpecDto dto) {
        return SarimaSpec.builder()
                .period(dto.getPeriod())
                .phi(ParameterMapping.toModel(dto.getPhiList()))
                .d(dto.getD())
                .theta(ParameterMapping.toModel(dto.getThetaList()))
                .bphi(ParameterMapping.toModel(dto.getBphiList()))
                .bd(dto.getBd())
                .btheta(ParameterMapping.toModel(dto.getBthetaList()))
                .build();
    }

    public static SarimaSpecDto toDto(SarimaSpec spec) {
        SarimaSpecDto.Builder builder = SarimaSpecDto.newBuilder()
                .setPeriod(spec.getPeriod())
                .setD(spec.getD())
                .setBd(spec.getBd());

        Parameter[] p = spec.getPhi();
        for (Parameter parameter : p) {
            builder.addPhi(ParameterMapping.toDto(parameter));
        }
        p = spec.getTheta();
        for (Parameter parameter : p) {
            builder.addTheta(ParameterMapping.toDto(parameter));
        }
        p = spec.getBphi();
        for (Parameter parameter : p) {
            builder.addBphi(ParameterMapping.toDto(parameter));
        }
        p = spec.getBtheta();
        for (Parameter parameter : p) {
            builder.addBtheta(ParameterMapping.toDto(parameter));
        }

        return builder.build();
    }

    public static AutoModelSpec toModel(AutoModelSpecDto dto) {
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

    public static AutoModelSpecDto toDto(AutoModelSpec spec) {
        AutoModelSpecDto.Builder builder = AutoModelSpecDto.newBuilder();

        builder.setEnabled(spec.isEnabled())
                .setEnabled(spec.isEnabled())
                .setCancel(spec.getCancel())
                .setUb1(spec.getUb1())
                .setUb2(spec.getUb2())
                .setPcr(spec.getPcr())
                .setPc(spec.getPc())
                .setTsig(spec.getTsig())
                .setAcceptDef(spec.isAcceptDefault())
                .setAmiCompare(spec.isAmiCompare());

        return builder.build();
    }

    public static RegressionSpec toModel(RegressionSpecDto dto, double tc) {
        CalendarSpec.Builder cBuilder = CalendarSpec.builder();

        if (dto.hasEaster()) {
            cBuilder.easter(toModel(dto.getEaster()));
        }

        if (dto.hasTd()) {
            cBuilder.tradingDays(toModel(dto.getTd()));
        }

        MeanSpec mean = MeanSpec.none();
        if (dto.hasMean()) {
            Parameter p = ParameterMapping.toModel(dto.getMean());
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
            builder.outlier(VariableMapping.toModel(outlier, tc));
        }

        n = dto.getUsersCount();
        for (int i = 0; i < n; ++i) {
            TsVariableDto var = dto.getUsers(i);
            builder.userDefinedVariable(VariableMapping.toModel(var));
        }

        n = dto.getInterventionsCount();
        for (int i = 0; i < n; ++i) {
            InterventionVariableDto var = dto.getInterventions(i);
            builder.interventionVariable(VariableMapping.toModel(var));
        }

        n = dto.getRampsCount();
        for (int i = 0; i < n; ++i) {
            RampDto var = dto.getRamps(i);
            builder.ramp(VariableMapping.toModel(var));
        }

        return builder
                .build();
    }

    public static RegressionSpecDto toDto(RegressionSpec spec) {
        RegressionSpecDto.Builder builder = RegressionSpecDto.newBuilder()
                .setEaster(toDto(spec.getCalendar().getEaster()))
                .setTd(toDto(spec.getCalendar().getTradingDays()));

        MeanSpec mean = spec.getMean();
        if ( mean.isUsed()){
            builder.setMean(ParameterMapping.toDto(mean.getCoefficient()))
                    .setCheckMean(mean.isTest());
        } else {
            // unused -> Parameter = unused
            builder.clearMean().setCheckMean(false);
        }

        List<Variable<IOutlier>> outliers = spec.getOutliers();
        outliers.forEach(outlier -> {
            builder.addOutliers(VariableMapping.toOutlierDto(outlier));
        });
        List<Variable<TsContextVariable>> users = spec.getUserDefinedVariables();
        users.forEach(user -> {
            builder.addUsers(VariableMapping.toTsVariableDto(user));
        });
        List<Variable<InterventionVariable>> ivs = spec.getInterventionVariables();
        ivs.forEach(iv -> {
            builder.addInterventions(VariableMapping.toInterventionVariableDto(iv));
        });
        List<Variable<Ramp>> ramps = spec.getRamps();
        ramps.forEach(ramp -> {
            builder.addRamps(VariableMapping.toRampDto(ramp));
        });

        return builder.build();
    }

    public static EasterSpec toModel(EasterSpecDto dto) {
        var builder = EasterSpec.builder()
                .duration(dto.getDuration())
                .type(EnumsMapping.toModel(dto.getType()))
                .test(dto.getTest())
                .julian(dto.getJulian());

        if (dto.hasCoefficient())
            builder.coefficient(ParameterMapping.toModel(dto.getCoefficient()));

        return builder.build();
    }

    public static EasterSpecDto toDto(EasterSpec spec) {
        EasterSpecDto.Builder builder = EasterSpecDto.newBuilder()
                .setType(EnumsMapping.toDto(spec.getType()))
                .setDuration(spec.getDuration())
                .setJulian(spec.isJulian())
                .setTest(spec.isTest());

        Parameter c = spec.getCoefficient();
        if (c != null)
            builder.setCoefficient(ParameterMapping.toDto(c));
        return builder.build();
    }

    public static TradingDaysSpec toModel(TradingDaysSpecDto dto) {
        String holidays = dto.getHolidays();
        TradingDaysType td = EnumsMapping.toModel(dto.getTd());
        LengthOfPeriodType lp = EnumsMapping.toModel(dto.getLp());
        Parameter lpc = ParameterMapping.toModel(dto.getLpcoefficient());
        Parameter[] tdc = ParameterMapping.toModel(dto.getTdcoefficientsList());
        boolean test = isTest(dto);

        if (!holidays.isEmpty()) {
            TradingDaysSpec.AutoMethod auto = EnumsMapping.toModel(dto.getAuto());
            if (auto != TradingDaysSpec.AutoMethod.UNUSED) {
                return TradingDaysSpec.automaticHolidays(holidays, lp, auto, dto.getPtest(), dto.getAutoAdjust());
            }
            if (test) {
                return TradingDaysSpec.holidays(holidays, td, lp, EnumsMapping.toModel(dto.getTest()), dto.getAutoAdjust());
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
                return TradingDaysSpec.userDefined(users, EnumsMapping.toModel(dto.getTest()));
            } else {
                return TradingDaysSpec.userDefined(users, tdc);
            }
        }

        int w = dto.getW();
        if (w > 0) {
            return TradingDaysSpec.stockTradingDays(w, EnumsMapping.toModel(dto.getTest()));
        }

        TradingDaysSpec.AutoMethod auto = EnumsMapping.toModel(dto.getAuto());
        if (auto != TradingDaysSpec.AutoMethod.UNUSED) {
            return TradingDaysSpec.automatic(lp, auto, dto.getPtest(), dto.getAutoAdjust());
        } else if (td == TradingDaysType.NONE) {
            return TradingDaysSpec.none();
        } else {
            if (test) {
                return TradingDaysSpec.td(td, lp, EnumsMapping.toModel(dto.getTest()), dto.getAutoAdjust());
            } else {
                return TradingDaysSpec.td(td, lp, tdc, lpc);
            }
        }
    }

    private static void fill(TradingDaysSpec spec, TradingDaysSpecDto.Builder builder) {
        String holidays = spec.getHolidays();
        if (holidays != null) {
            builder.setHolidays(holidays)
                    .setLp(EnumsMapping.toDto(spec.getLengthOfPeriodType()))
                    .setTd(EnumsMapping.toDto(spec.getTradingDaysType()))
                    .setAuto(EnumsMapping.toDto(spec.getAutomaticMethod()))
                    .setTest(EnumsMapping.toDto((spec.getRegressionTestType())))
                    .setAutoAdjust(spec.isAutoAdjust())
                    .setPtest(spec.getProbabilityForFTest());
            return;
        }

        String[] userVariables = spec.getUserVariables();
        if (userVariables != null && userVariables.length > 0) {
            for (String v : userVariables) {
                builder.addUsers(v);
            }
            builder.setTest(EnumsMapping.toDto(spec.getRegressionTestType()));
            return;
        }

        int w = spec.getStockTradingDays();
        if (w > 0) {
            builder.setW(w)
                    .setTest(EnumsMapping.toDto(spec.getRegressionTestType()));
            return;
        }
        if (spec.isAutomatic()) {
            builder.setLp(EnumsMapping.toDto(spec.getLengthOfPeriodType()))
                    .setTd(EnumsMapping.toDto(spec.getTradingDaysType()))
                    .setTest(EnumsMapping.toDto(spec.getRegressionTestType()))
                    .setAuto(EnumsMapping.toDto(spec.getAutomaticMethod()))
                    .setPtest(spec.getProbabilityForFTest());

        } else {
            builder.setLp(EnumsMapping.toDto(spec.getLengthOfPeriodType()))
                    .setTd(EnumsMapping.toDto(spec.getTradingDaysType()))
                    .setTest(EnumsMapping.toDto(spec.getRegressionTestType()));
        }
    }

    public static TradingDaysSpecDto toDto(TradingDaysSpec spec) {
        TradingDaysSpecDto.Builder builder = TradingDaysSpecDto.newBuilder();
        fill(spec, builder);
        return builder.build();
    }

    private static boolean isTest(TradingDaysSpecDto dto) {
        return dto.getAuto() != AutomaticTradingDays.TD_AUTO_NO
                || dto.getTest() == TradingDaysTest.TD_TEST_JOINT_F
                || dto.getTest() == TradingDaysTest.TD_TEST_SEPARATE_T;
    }
}
