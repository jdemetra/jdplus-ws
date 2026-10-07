package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.MatrixDto;
import jdplus.toolkit.base.api.data.Iterables;
import jdplus.toolkit.base.api.math.matrices.Matrix;

public class MatrixMapping {
    public static MatrixDto toDto(Matrix m) {
       if (m == null || m.isEmpty()){
           return MatrixDto.getDefaultInstance();
       }

        return MatrixDto
                .newBuilder()
                .setNrows(m.getRowsCount())
                .setNcols(m.getColumnsCount())
                .addAllValues(Iterables.of(m.toArray()))
                .build();
    }
}
