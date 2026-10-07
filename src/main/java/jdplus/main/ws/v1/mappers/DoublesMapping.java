package jdplus.main.ws.v1.mappers;

import jdplus.toolkit.base.api.data.DoubleSeq;

import java.util.List;

public class DoublesMapping {
    public static List<Double> toDto(DoubleSeq value) {
        return value.stream().boxed().toList();
    }

    public static DoubleSeq toModel(List<Double> value) {
        return DoubleSeq.onMapping(value.size(), value::get);
    }
}
