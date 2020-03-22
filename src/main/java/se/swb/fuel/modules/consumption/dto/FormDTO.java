package se.swb.fuel.modules.consumption.dto;

import lombok.Data;

@Data
public class FormDTO {
    private String type;
    private String date;
    private double price;
    private double volume;
    private long driverId;
}
