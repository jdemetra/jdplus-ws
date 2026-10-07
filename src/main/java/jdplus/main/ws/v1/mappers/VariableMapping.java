package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.InterventionVariableDto;
import jdplus.main.ws.v1.OutlierDto;
import jdplus.main.ws.v1.RampDto;
import jdplus.main.ws.v1.TsVariableDto;
import jdplus.toolkit.base.api.data.Parameter;
import jdplus.toolkit.base.api.data.Range;
import jdplus.toolkit.base.api.timeseries.regression.*;

import java.time.LocalDate;

public class VariableMapping {
    public static TsVariableDto toDto(Variable<TsContextVariable> v) {
        return TsVariableDto.newBuilder()
                .setName(v.getName())
                .setId(v.getCore().getId())
                .setLag(v.getCore().getLag())
                .setCoefficient(ParameterMapping.toDto(v.getCoefficient(0)))
                .putAllMetadata(v.getAttributes())
                .build();
    }

    public static Variable<IOutlier> toModel(OutlierDto outlier, double tc) {
        LocalDate ldt = LocalDateMapping.toModel(outlier.getPosition());
        IOutlier o;
        switch (outlier.getCode()) {
            case "ao":
            case "AO":
                o = new AdditiveOutlier(ldt.atStartOfDay());
                break;
            case "ls":
            case "LS":
                o = new LevelShift(ldt.atStartOfDay(), true);
                break;
            case "tc":
            case "TC":
                o = new TransitoryChange(ldt.atStartOfDay(), tc);
                break;
            case "so":
            case "SO":
                o = new PeriodicOutlier(ldt.atStartOfDay(), 0, true);
                break;

            default:
                return null;
        }
        Parameter c = ParameterMapping.toModel(outlier.getCoefficient());
        return Variable.<IOutlier>builder()
                .core(o)
                .name(outlier.getName())
                .coefficients(c == null ? null : new Parameter[]{c})
                .attributes(outlier.getMetadataMap())
                .build();
    }

    public static Variable<TsContextVariable> toModel(TsVariableDto v) {
        Parameter c = ParameterMapping.toModel(v.getCoefficient());
        return Variable.<TsContextVariable>builder()
                .name(v.getName())
                .core(new TsContextVariable(v.getId(), v.getLag()))
                .attributes(v.getMetadataMap())
                .coefficients(c == null ? null : new Parameter[]{c})
                .build();
    }

    public static Variable<Ramp> toModel(RampDto v) {
        LocalDate start = LocalDateMapping.toModel(v.getStart());
        LocalDate end = LocalDateMapping.toModel(v.getEnd());
        Parameter c = ParameterMapping.toModel(v.getCoefficient());
        return Variable.<Ramp>builder()
                .name(v.getName())
                .core(new Ramp(start.atStartOfDay(), end.atStartOfDay()))
                .attributes(v.getMetadataMap())
                .coefficients(c == null ? null : new Parameter[]{c})
                .build();
    }

    public static Variable<InterventionVariable> toModel(InterventionVariableDto v) {
        InterventionVariable.Builder builder = InterventionVariable.builder()
                .delta(v.getDelta())
                .deltaSeasonal(v.getSeasonalDelta());
        int n = v.getSequencesCount();
        for (int i = 0; i < n; ++i) {
            InterventionVariableDto.SequenceDto seq = v.getSequences(i);
            LocalDate start = LocalDateMapping.toModel(seq.getStart());
            LocalDate end = LocalDateMapping.toModel(seq.getEnd());
            builder.sequence(Range.of(start.atStartOfDay(), end.atStartOfDay()));
        }
        Parameter c = ParameterMapping.toModel(v.getCoefficient());
        return Variable.<InterventionVariable>builder()
                .name(v.getName())
                .core(builder.build())
                .coefficients(c == null ? null : new Parameter[]{c})
                .attributes(v.getMetadataMap())
                .build();
    }
}
