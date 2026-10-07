package jdplus.main.ws.v1.mappers;

import jdplus.main.ws.v1.TsMonikerDto;
import jdplus.toolkit.base.api.timeseries.TsMoniker;

public class TsMonikerMapping {
    public static TsMoniker toModel(TsMonikerDto moniker) {
        return TsMoniker.of(moniker.getSource(), moniker.getId());
    }
}
