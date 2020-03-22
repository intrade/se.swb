package se.swb.fuel.modules.consumption.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.swb.fuel.modules.consumption.models.FuelType;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupedStatsDto {
    private int month;
    private int year;
    private FuelType type;
    private double totalVolume;
    private BigDecimal totalCost;
    private BigDecimal averagePrice;

    public GroupedStatsDto(int month, int year, String type, double totalVolume, BigDecimal totalCost, BigDecimal averagePrice) {
        this.month = month;
        this.year = year;
        this.type = FuelType.valueOf(type);
        this.totalVolume = totalVolume;
        this.totalCost = totalCost;
        this.averagePrice = averagePrice;
    }
}
