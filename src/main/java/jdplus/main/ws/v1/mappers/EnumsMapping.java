package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.*;
import jdplus.toolkit.base.api.data.ParameterType;
import jdplus.toolkit.base.api.modelling.TransformationType;
import jdplus.toolkit.base.api.timeseries.TimeSelector;
import jdplus.toolkit.base.api.timeseries.TsDataTable;
import jdplus.toolkit.base.api.timeseries.TsUnit;
import jdplus.toolkit.base.api.timeseries.calendars.DayEvent;
import jdplus.toolkit.base.api.timeseries.calendars.LengthOfPeriodType;
import jdplus.toolkit.base.api.timeseries.calendars.TradingDaysType;
import jdplus.tramoseats.base.api.tramo.EasterSpec;
import jdplus.tramoseats.base.api.tramo.RegressionTestType;
import jdplus.tramoseats.base.api.tramo.TradingDaysSpec;

public class EnumsMapping {
    public static DayEvent toModel(CalendarEvent hol) {
        return switch (hol) {
            case HOLIDAY_NEWYEAR -> DayEvent.NewYear;
            case HOLIDAY_SHROVEMONDAY -> DayEvent.ShroveMonday;
            case HOLIDAY_SHROVETUESDAY -> DayEvent.ShroveTuesday;
            case HOLIDAY_ASHWEDNESDAY -> DayEvent.AshWednesday;
            case HOLIDAY_EASTER -> DayEvent.Easter;
            case HOLIDAY_MAUNDYTHURSDAY -> DayEvent.MaundyThursday;
            case HOLIDAY_GOODFRIDAY -> DayEvent.GoodFriday;
            case HOLIDAY_EASTERMONDAY -> DayEvent.EasterMonday;
            case HOLIDAY_ASCENSION -> DayEvent.Ascension;
            case HOLIDAY_PENTECOST -> DayEvent.Pentecost;
            case HOLIDAY_CORPUSCHRISTI -> DayEvent.CorpusChristi;
            case HOLIDAY_WHITMONDAY -> DayEvent.WhitMonday;
            case HOLIDAY_MAYDAY -> DayEvent.MayDay;
            case HOLIDAY_ASSUMPTION -> DayEvent.Assumption;
            case HOLIDAY_LABORDAY -> DayEvent.LaborDay;
            case HOLIDAY_HALLOWEEN -> DayEvent.Halloween;
            case HOLIDAY_ALLSAINTSDAY -> DayEvent.AllSaintsDay;
            case HOLIDAY_ARMISTICE -> DayEvent.Armistice;
            case HOLIDAY_THANKSGIVING -> DayEvent.ThanksGiving;
            case HOLIDAY_CHRISTMAS -> DayEvent.Christmas;
            default -> null;
        };
    }

    public static TradingDaysSpec.AutoMethod toModel(AutomaticTradingDays value) {
        return switch (value) {
            case TD_AUTO_FTEST -> TradingDaysSpec.AutoMethod.FTEST;
            case TD_AUTO_WALD -> TradingDaysSpec.AutoMethod.WALD;
            case TD_AUTO_AIC -> TradingDaysSpec.AutoMethod.AIC;
            case TD_AUTO_BIC -> TradingDaysSpec.AutoMethod.BIC;
            default -> TradingDaysSpec.AutoMethod.UNUSED;
        };
    }

    public static RegressionTestType toModel(TradingDaysTest value) {
        return switch (value) {
            case TD_TEST_JOINT_F -> RegressionTestType.Joint_F;
            case TD_TEST_SEPARATE_T -> RegressionTestType.Separate_T;
            default -> RegressionTestType.None;
        };
    }

    public static TradingDaysType toModel(TradingDays value) {
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

    public static EasterSpec.Type toModel(EasterType value) {
        return switch (value) {
            case EASTER_STANDARD -> EasterSpec.Type.Standard;
            case EASTER_INCLUDEEASTER -> EasterSpec.Type.IncludeEaster;
            case EASTER_INCLUDEEASTERMONDAY -> EasterSpec.Type.IncludeEasterMonday;
            default -> EasterSpec.Type.Unused;
        };
    }

    public static TransformationType toModel(Transformation value) {
        return switch (value) {
            case FN_LOG -> TransformationType.Log;
            case FN_AUTO -> TransformationType.Auto;
            default -> TransformationType.None;
        };
    }

    public static LengthOfPeriodType toModel(LengthOfPeriod value) {
        return switch (value) {
            case LP_LEAPYEAR -> LengthOfPeriodType.LeapYear;
            case LP_LENGTHOFPERIOD -> LengthOfPeriodType.LengthOfPeriod;
            default -> LengthOfPeriodType.None;
        };
    }

    public static TimeSelector.SelectionType toModel(SelectionType value) {
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

    public static DistributionType toDto(TsDataTable.DistributionType value) {
        return switch (value) {
            case FIRST -> DistributionType.DIST_FIRST;
            case LAST -> DistributionType.DIST_LAST;
            case MIDDLE -> DistributionType.DIST_MIDDLE;
        };
    }

    public static TsDataTable.DistributionType toModel(DistributionType value) {
        return switch (value) {
            case DIST_FIRST -> TsDataTable.DistributionType.FIRST;
            case DIST_LAST -> TsDataTable.DistributionType.LAST;
            case DIST_MIDDLE -> TsDataTable.DistributionType.MIDDLE;
            default -> throw new IllegalArgumentException(value.name());
        };
    }

    public static ValueStatus toDto(TsDataTable.ValueStatus value) {
        return switch (value) {
            case PRESENT -> ValueStatus.VS_PRESENT;
            case UNUSED -> ValueStatus.VS_UNUSED;
            case BEFORE -> ValueStatus.VS_BEFORE;
            case AFTER -> ValueStatus.VS_AFTER;
            case EMPTY -> ValueStatus.VS_EMPTY;
        };
    }

    public static TsDataTable.ValueStatus toModel(ValueStatus value) {
        return switch (value) {
            case VS_PRESENT -> TsDataTable.ValueStatus.PRESENT;
            case VS_UNUSED -> TsDataTable.ValueStatus.UNUSED;
            case VS_BEFORE -> TsDataTable.ValueStatus.BEFORE;
            case VS_AFTER -> TsDataTable.ValueStatus.AFTER;
            case VS_EMPTY -> TsDataTable.ValueStatus.EMPTY;
            default -> throw new IllegalArgumentException(value.name());
        };
    }

    public static jdplus.main.ws.v1.AggregationType toDto(jdplus.toolkit.base.api.data.AggregationType value) {
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

    public static jdplus.toolkit.base.api.data.AggregationType toModel(jdplus.main.ws.v1.AggregationType value) {
        return switch (value) {
            case AGGREGATION_NONE -> jdplus.toolkit.base.api.data.AggregationType.None;
            case AGGREGATION_SUM -> jdplus.toolkit.base.api.data.AggregationType.Sum;
            case AGGREGATION_AVERAGE -> jdplus.toolkit.base.api.data.AggregationType.Average;
            case AGGREGATION_FIRST -> jdplus.toolkit.base.api.data.AggregationType.First;
            case AGGREGATION_LAST -> jdplus.toolkit.base.api.data.AggregationType.Last;
            case AGGREGATION_MAX -> jdplus.toolkit.base.api.data.AggregationType.Max;
            case AGGREGATION_MIN -> jdplus.toolkit.base.api.data.AggregationType.Min;
            default -> throw new IllegalArgumentException(value.name());
        };
    }

    public static jdplus.main.ws.v1.ParameterType toDto(jdplus.toolkit.base.api.data.ParameterType value) {
        return switch (value) {
            case Initial -> jdplus.main.ws.v1.ParameterType.PARAMETER_INITIAL;
            case Fixed -> jdplus.main.ws.v1.ParameterType.PARAMETER_FIXED;
            case Estimated -> jdplus.main.ws.v1.ParameterType.PARAMETER_ESTIMATED;
            default -> jdplus.main.ws.v1.ParameterType.PARAMETER_UNDEFINED;
        };
    }

    public static jdplus.toolkit.base.api.data.ParameterType toModel(jdplus.main.ws.v1.ParameterType value) {
        return switch (value) {
            case PARAMETER_UNDEFINED -> jdplus.toolkit.base.api.data.ParameterType.Undefined;
            case PARAMETER_INITIAL -> jdplus.toolkit.base.api.data.ParameterType.Initial;
            case PARAMETER_FIXED -> jdplus.toolkit.base.api.data.ParameterType.Fixed;
            case PARAMETER_ESTIMATED -> ParameterType.Estimated;
            default -> null;
        };
    }

    public static TsUnit toModel(Frequency value) {
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
}
