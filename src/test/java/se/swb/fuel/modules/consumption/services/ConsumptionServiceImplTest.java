package se.swb.fuel.modules.consumption.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.swb.fuel.modules.consumption.dto.FuelCostDTO;
import se.swb.fuel.modules.consumption.dto.GroupedStatsDto;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ConsumptionServiceImplTest {

    @Autowired
    private ConsumptionService consumptionService;

    @Test
    void listConsumptionsShouldReturnFourRecords() {
        assertThat(consumptionService.listConsumptions().size()).isEqualTo(4);
    }

    @Test
    void testSholdReturnTwoStatisticalRecords() {
        List<FuelCostDTO> stat = consumptionService.getMonthlyStatistic(Year.now());
        assertThat(stat.size()).isEqualTo(2);

        assertThat(stat.get(0).getMonth()).isEqualByComparingTo(Month.JANUARY);
        assertThat(stat.get(0).getYear()).isEqualByComparingTo(Year.parse("2020"));
        assertThat(stat.get(0).getTotalCost()).isEqualTo("108.945");

    }

    @Test
    void testSholdReturnOneStatisticalRecordsForFeb() {
        List<FuelCostDTO> stat = consumptionService.getMonthlyStatistic(Year.now(), Month.FEBRUARY);
        assertThat(stat.size()).isEqualTo(1);

        assertThat(stat.get(0).getMonth()).isEqualByComparingTo(Month.FEBRUARY);
        assertThat(stat.get(0).getYear()).isEqualByComparingTo(Year.parse("2020"));
        assertThat(stat.get(0).getTotalCost().doubleValue()).isEqualTo(29);

    }

    @Test
    void testingGroupedStatsWithOutDriverID(){
        List<GroupedStatsDto> stats = consumptionService.getGroupedStats(Optional.empty());
        assertThat(stats.size()).isEqualTo(3);

        GroupedStatsDto dto = stats.get(0);
        assertThat(dto.getMonth()).isEqualTo(1);
        assertThat(dto.getTotalVolume()).isEqualTo(80.5);
        assertThat(dto.getTotalCost().doubleValue()).isCloseTo(96.64, within(0.005));
    }

    @Test
    void testingGroupedStatsWithDriverID(){
        List<GroupedStatsDto> stats = consumptionService.getGroupedStats(Optional.of(3));
        assertThat(stats.size()).isEqualTo(2);

        GroupedStatsDto dto = stats.get(1);
        assertThat(dto.getMonth()).isEqualTo(2);
        assertThat(dto.getTotalVolume()).isEqualTo(25);
        assertThat(dto.getTotalCost().doubleValue()).isCloseTo(28.99, within(0.01));
        assertThat(dto.getAveragePrice().doubleValue()).isEqualTo(1.16);
    }


}
