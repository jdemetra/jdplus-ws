package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.DateDto;

import java.time.LocalDateTime;

public class LocalDateTimeMapping {
    public static LocalDateTime toModel(DateDto value) {
        return LocalDateTime.of(value.getYear(), value.getMonth(), value.getDay(), 0, 0, 0);
    }
}
