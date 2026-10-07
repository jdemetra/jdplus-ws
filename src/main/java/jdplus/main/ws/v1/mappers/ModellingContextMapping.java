package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.CalendarDefinitionDto;
import jdplus.main.ws.v1.ModellingContextDto;
import jdplus.main.ws.v1.TsDataSuppliersDto;
import jdplus.toolkit.base.api.timeseries.regression.ModellingContext;

import java.util.Map;

public class ModellingContextMapping {
    public static ModellingContext toModel(ModellingContextDto dto) {
        ModellingContext result = new ModellingContext();

        Map<String, CalendarDefinitionDto> cmgr = dto.getCalendarsMap();
        for (Map.Entry<String, CalendarDefinitionDto> entry : cmgr.entrySet()) {
            result.getCalendars().set(entry.getKey(), CalendarsMapping.toModel(entry.getValue()));
        }

        Map<String, TsDataSuppliersDto> smap = dto.getVariablesMap();
        for (Map.Entry<String, TsDataSuppliersDto> entry : smap.entrySet()) {
            result.getTsVariableManagers().set(entry.getKey(), TsDataSuppliersMapping.toModel(entry.getValue()));
        }

        return result;
    }
}
