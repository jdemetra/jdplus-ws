package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.DateDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateMapping {
    public static DateDto toDto(LocalDate value) {
        return DateDto
                .newBuilder()
                .setYear(value.getYear())
                .setMonth(value.getMonthValue())
                .setDay(value.getDayOfMonth())
                .build();
    }

    public static LocalDate toModel(DateDto value) {
        return LocalDate.of(value.getYear(), value.getMonth(), value.getDay());
    }
}
