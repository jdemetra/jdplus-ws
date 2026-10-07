package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.DateDto;

import java.time.LocalDate;

public class LocalDateMapping {
    public static DateDto toDto(LocalDate ld) {
        if (ld.equals(LocalDate.MIN)) {
            return DateDto.newBuilder()
                    .setYear(1)
                    .setMonth(1)
                    .setDay(1)
                    .build();
        } else if (ld.equals(LocalDate.MAX)) {
            return DateDto.newBuilder()
                    .setYear(9999)
                    .setMonth(12)
                    .setDay(31)
                    .build();
        } else {
            return DateDto.newBuilder()
                    .setYear(ld.getYear())
                    .setMonth(ld.getMonthValue())
                    .setDay(ld.getDayOfMonth())
                    .build();
        }
    }

    public static LocalDate toModel(DateDto d) {
        return switch (d.getYear()) {
            case 0 -> throw new IllegalArgumentException("Date not correctly initialized");
            case 1 -> LocalDate.MIN;
            case 9999 -> LocalDate.MAX;
            default -> LocalDate.of(d.getYear(), d.getMonth(), d.getDay());
        };
    }
}
