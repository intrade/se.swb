package se.swb.fuel.modules.consumption.dto;

import lombok.Data;
import se.swb.fuel.modules.consumption.models.FuelType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ConsumptionDTO {
    private long id;
    private FuelType type;
    private LocalDate date;
    private BigDecimal price;
    private double volume;
    private long driverId;
    private BigDecimal totalCost;
}
