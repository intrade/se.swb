package se.swb.fuel.modules.consumption.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.swb.fuel.modules.consumption.dto.GroupedStatsDto;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NamedNativeQuery(query = "SELECT MONTH(m.date) as month, YEAR(m.date) as year, m.type, sum(m.volume) as total_volume, sum(m.price*m.volume) as total_cost, (sum((m.price*m.volume))/sum(m.volume)) as average_price FROM CONSUMPTION m group by MONTH(m.date), YEAR(m.date), m.type",
        resultSetMapping = "GroupedStatsDto",
        name = "groupedStatsDto")
@NamedNativeQuery(query = "SELECT MONTH(m.date) as month, YEAR(m.date) as year, m.type, sum(m.volume) as total_volume, sum(m.price*m.volume) as total_cost, (sum((m.price*m.volume))/sum(m.volume)) as average_price FROM CONSUMPTION m where m.driver_id = :driver group by MONTH(m.date), YEAR(m.date), m.type",
        resultSetMapping = "GroupedStatsDto",
        name = "paramGroupedStatsDto")
@SqlResultSetMapping(
        name = "GroupedStatsDto",
        classes = {
                @ConstructorResult(
                        targetClass = GroupedStatsDto.class,
                        columns = {
                                @ColumnResult(name="month", type = Integer.class),
                                @ColumnResult(name = "year", type = Integer.class),
                                @ColumnResult(name = "type", type = String.class),
                                @ColumnResult(name = "total_volume", type = Double.class),
                                @ColumnResult(name = "total_cost", type = BigDecimal.class),
                                @ColumnResult(name = "average_price", type = BigDecimal.class)
                        }
                )
        }
)

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consumption {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqCons")
    @SequenceGenerator(allocationSize = 10, initialValue = 1, name = "seqCons")
    private long id;
    @Enumerated(EnumType.STRING)
    private FuelType type;
    private LocalDate date;
    private BigDecimal price;
    private double volume;
    private long driverId;

    public Consumption(String type, String date, String price, String volume, String driverId) {
        this.type = FuelType.valueOf(type);
        this.date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        this.price = new BigDecimal(price);
        this.volume = Double.valueOf(volume);
        this.driverId = Long.parseLong(driverId);
    }
}
