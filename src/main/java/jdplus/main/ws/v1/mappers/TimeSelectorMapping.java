package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.SelectionType;
import jdplus.main.ws.v1.TimeSelectorDto;
import jdplus.toolkit.base.api.timeseries.TimeSelector;

import java.time.LocalDate;

public class TimeSelectorMapping {
    public static TimeSelectorDto toDto(TimeSelector sel) {
        TimeSelectorDto.Builder builder = TimeSelectorDto.newBuilder();

        switch (sel.getType()) {
            case All:
                builder.setType(SelectionType.SPAN_ALL);
                break;
            case From:
                builder.setType(SelectionType.SPAN_FROM)
                        .setD0(LocalDateMapping.toDto(sel.getD0().toLocalDate()));
                break;
            case To:
                builder.setType(SelectionType.SPAN_TO)
                        .setD1(LocalDateMapping.toDto(sel.getD1().toLocalDate()));
                break;
            case Between:
                builder.setType(SelectionType.SPAN_BETWEEN)
                        .setD0(LocalDateMapping.toDto(sel.getD0().toLocalDate()))
                        .setD1(LocalDateMapping.toDto(sel.getD1().toLocalDate()));
                break;
            case First:
                builder.setType(SelectionType.SPAN_FIRST)
                        .setN0(sel.getN0());
                break;
            case Last:
                builder.setType(SelectionType.SPAN_LAST)
                        .setN1(sel.getN1());
                break;
            case Excluding:
                builder.setType(SelectionType.SPAN_EXCLUDING)
                        .setN0(sel.getN0())
                        .setN1(sel.getN1());
                break;

            default:
                builder.setType(SelectionType.SPAN_NONE);
        }

        return builder.build();
    }

    public static TimeSelector toModel(TimeSelectorDto sel) {
        switch (sel.getType()) {
            case SPAN_ALL:
                return TimeSelector.all();
            case SPAN_FROM: {
                LocalDate ld = LocalDateMapping.toModel(sel.getD0());
                return TimeSelector.from(ld.atStartOfDay());
            }
            case SPAN_TO: {
                LocalDate ld = LocalDateMapping.toModel(sel.getD1());
                return TimeSelector.to(ld.atStartOfDay());
            }
            case SPAN_BETWEEN: {
                LocalDate ld0 = LocalDateMapping.toModel(sel.getD0());
                LocalDate ld1 = LocalDateMapping.toModel(sel.getD1());
                return TimeSelector.between(ld0.atStartOfDay(), ld1.atStartOfDay());
            }
            case SPAN_FIRST: {
                int n0 = sel.getN0();
                return TimeSelector.first(n0);
            }
            case SPAN_LAST: {
                int n1 = sel.getN1();
                return TimeSelector.last(n1);
            }
            case SPAN_EXCLUDING: {
                int n0 = sel.getN0(), n1 = sel.getN1();
                return TimeSelector.excluding(n0, n1);
            }
            default:
                return TimeSelector.none();
        }
    }


}
