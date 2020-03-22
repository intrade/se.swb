package se.swb.fuel.modules.consumption.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;

@Data
@AllArgsConstructor
public class FuelCostDTO {
    private Month month;
    private Year year;
    private BigDecimal totalCost;
}
