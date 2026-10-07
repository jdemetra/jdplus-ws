package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.Frequency;
import jdplus.toolkit.base.api.timeseries.TsUnit;

public class TsUnitMapping {
    public static Frequency toDto(TsUnit unit) {
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
}
