package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.TsDataSuppliersDto;
import jdplus.toolkit.base.api.timeseries.DynamicTsDataSupplier;
import jdplus.toolkit.base.api.timeseries.StaticTsDataSupplier;
import jdplus.toolkit.base.api.timeseries.TsDataSupplier;
import jdplus.toolkit.base.api.timeseries.regression.TsDataSuppliers;

public class TsDataSuppliersMapping {
    public static TsDataSuppliers toModel(TsDataSuppliersDto dto) {
        TsDataSuppliers s = new TsDataSuppliers();
        for (TsDataSuppliersDto.ItemDto item : dto.getItemsList()) {
            s.set(item.getName(), toModel(item));
        }
        return s;
    }

    private static TsDataSupplier toModel(TsDataSuppliersDto.ItemDto dto) {
        if (dto.hasData()) {
            return new StaticTsDataSupplier(TsDataMapping.toModel(dto.getData()));
        } else if (dto.hasDynamicData()) {
            return new DynamicTsDataSupplier(TsMonikerMapping.toModel(dto.getDynamicData().getMoniker()), TsDataMapping.toModel(dto.getDynamicData().getCurrent()));
        } else {
            return null;
        }
    }
}
