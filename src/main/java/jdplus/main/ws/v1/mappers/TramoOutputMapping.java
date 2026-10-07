package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.*;
import jdplus.toolkit.base.api.data.DoubleSeq;
import jdplus.toolkit.base.api.data.Iterables;
import jdplus.toolkit.base.api.math.matrices.Matrix;
import jdplus.toolkit.base.api.processing.ProcessingLog;
import jdplus.toolkit.base.api.stats.StatisticalTest;
import jdplus.toolkit.base.api.timeseries.TsData;
import jdplus.tramoseats.base.core.tramo.TramoOutput;

public class TramoOutputMapping {
    public static TramoOutputDto toDto(TramoOutput model) {
        TramoOutputDto.Builder builder = TramoOutputDto.newBuilder()
                .setEstimationSpec(SpecsMapping.toDto(model.getEstimationSpec()));

        if (model.getResult() != null) {
            builder.setResult(RegSarimaModelMapping.toDto(model.getResult()))
                    .setResultSpec(SpecsMapping.toDto(model.getResultSpec()));
        }
        // TODO detail ?

        if (model.getLogs() != null) {
            builder.setLog(toDto(model.getLogs()));
        }

        return builder.build();
    }

    public static ProcessingLogsDto toDto(ProcessingLog model) {
        return ProcessingLogsDto.newBuilder()
                .addAllLog(model.all().stream().map(TramoOutputMapping::toDto).toList())
                .build();
    }

    public static ProcessingInformationDto toDto(ProcessingLog.Information model) {
        return ProcessingInformationDto.newBuilder()
                .setName(model.getName())
                .setOrigin(model.getOrigin())
                .setMsg(model.getMsg())
                .setType(EnumsMapping.toDto(model.getType()))
                .setDetails(toDto(model.getDetails()))
                .build();
    }

    public static ProcessingDetailDto toDto(Object data) {
        ProcessingDetailDto.Builder builder = ProcessingDetailDto.newBuilder();

        if (data != null) {
            switch (data) {
                case TsData tsData -> builder.setTs(TsDataMapping.toDto(tsData));
                case Matrix matrix -> builder.setMatrix(MatrixMapping.toDto(matrix));
                case DoubleSeq seq -> builder.setArray(DoublesDto.newBuilder().addAllValues(Iterables.of(seq)).build());
                case StatisticalTest test -> builder.setTest(StatisticalTestMapping.toDto(test));
                case Double d -> builder.setDvalue(d);
                case Integer integer -> builder.setIvalue(integer);
                default -> {
                }
            }
        }

        return builder.build();
    }
}
