package se.swb.fuel.modules.consumption.converters;

import se.swb.fuel.modules.consumption.dto.ConsumptionDTO;
import se.swb.fuel.modules.consumption.models.Consumption;

import java.math.BigDecimal;

public class ConsumptionToDto {
    public static ConsumptionDTO convert(Consumption c){
        ConsumptionDTO dto = new ConsumptionDTO();
        dto.setId(c.getId());
        dto.setDate(c.getDate());
        dto.setDriverId(c.getDriverId());
        dto.setPrice(c.getPrice());
        dto.setVolume(c.getVolume());
        dto.setType(c.getType());
        dto.setTotalCost(c.getPrice().multiply(BigDecimal.valueOf(c.getVolume())));
        return dto;
    }
}
