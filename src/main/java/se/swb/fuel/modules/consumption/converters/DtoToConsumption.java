package se.swb.fuel.modules.consumption.converters;

import se.swb.fuel.modules.consumption.dto.ConsumptionDTO;
import se.swb.fuel.modules.consumption.models.Consumption;

public class DtoToConsumption {
    public static Consumption convert(ConsumptionDTO dto){
        Consumption c = new Consumption();
        c.setId(dto.getId());
        c.setType(dto.getType());
        c.setVolume(dto.getVolume());
        c.setPrice(dto.getPrice());
        c.setDriverId(dto.getDriverId());
        c.setDate(dto.getDate());
        return c;
    }
}
