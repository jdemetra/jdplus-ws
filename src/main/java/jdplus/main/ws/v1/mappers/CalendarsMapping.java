package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.*;
import jdplus.toolkit.base.api.timeseries.ValidityPeriod;
import jdplus.toolkit.base.api.timeseries.calendars.*;
import jdplus.toolkit.base.api.util.WeightedItem;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class CalendarsMapping {
    public static CalendarDefinition toModel(CalendarDefinitionDto dto) {
        if (dto.hasCalendar()) {
            return toModel(dto.getCalendar());
        } else if (dto.hasChainedCalendar()) {
            return toModel(dto.getChainedCalendar());
        } else if (dto.hasWeightedCalendar()) {
            return toModel(dto.getWeightedCalendar());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static CompositeCalendar toModel(WeightedCalendarDto dto) {
        WeightedItem[] items = dto.getItemsList().stream()
                .map(item -> new WeightedItem<String>(item.getCalendar(), item.getWeight()))
                .toArray(WeightedItem[]::new);
        return new CompositeCalendar(items);
    }

    private static ChainedCalendar toModel(ChainedCalendarDto dto) {
        return new ChainedCalendar(dto.getCalendar1(), dto.getCalendar2(), LocalDateMapping.toModel(dto.getBreakDate()));
    }

    private static Calendar toModel(CalendarDto dto) {
        List<Holiday> hol = new ArrayList<>();

        dto.getFixedDaysList().forEach(fd -> {
            hol.add(toModel(fd));
        });

        dto.getFixedWeekDaysList().forEach(fd -> {
            hol.add(toModel(fd));
        });

        dto.getEasterRelatedDaysList().forEach(ed -> {
            hol.add(toModel(ed));
        });

        dto.getPrespecifiedHolidaysList().forEach(pd -> {
            hol.add(toModel(pd));
        });

        dto.getSingleDatesList().forEach(sd -> {
            hol.add(toModel(sd));
        });

        return new Calendar(hol.toArray(Holiday[]::new), dto.getMeanCorrection());
    }

    private static FixedDay toModel(FixedDayDto dto) {
        return new FixedDay(dto.getMonth(), dto.getDay(), dto.getWeight(), toModel(dto.getValidity()));
    }

    private static SingleDate toModel(SingleDateDto dto) {
        return new SingleDate(LocalDateMapping.toModel(dto.getDate()), dto.getWeight());
    }

    private static FixedWeekDay toModel(FixedWeekDayDto dto) {
        return new FixedWeekDay(dto.getMonth(), dto.getPosition(), DayOfWeek.of(dto.getWeekday()), dto.getWeight(), toModel(dto.getValidity()));
    }

    private static EasterRelatedDay toModel(EasterRelatedDayDto dto) {
        if (dto.getJulian()) {
            return EasterRelatedDay.julian(dto.getOffset(), dto.getWeight(), toModel(dto.getValidity()));
        } else {
            return EasterRelatedDay.gregorian(dto.getOffset(), dto.getWeight(), toModel(dto.getValidity()));
        }
    }

    private static PrespecifiedHoliday toModel(PrespecifiedHolidayDto dto) {
        DayEvent ce;
        boolean julian = false;
        if (dto.getEvent() == CalendarEvent.HOLIDAY_JULIANEASTER){
            ce = DayEvent.Easter;
            julian = true;
        } else {
            ce = EnumsMapping.toModel(dto.getEvent());
        }
        return PrespecifiedHoliday.builder()
                .event(ce)
                .offset(dto.getOffset())
                .julian(julian)
                .weight(dto.getWeight())
                .validityPeriod(toModel(dto.getValidity()))
                .build();
    }

    private static ValidityPeriod toModel(ValidityPeriodDto dto) {
        return ValidityPeriod.between(LocalDateMapping.toModel(dto.getStart()), LocalDateMapping.toModel(dto.getEnd()));
    }
}
