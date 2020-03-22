package se.swb.fuel.modules.consumption.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se.swb.fuel.modules.consumption.models.Consumption;

import java.util.List;

@Repository
public interface ConsumptionJpaRepository extends JpaRepository<Consumption, Long> {

    @Query(value = "SELECT m.* from consumption m where m.date >= ?1", nativeQuery = true)
    List<Consumption> findAllFromDate(String date);

    @Query(value = "select m.* from consumption m where m.date >= ?1 and m.date <= ?2", nativeQuery = true)
    List<Consumption> findByPeriod(String dateStart, String dateEnd);

    @Query(value = "select m.* from consumption m where m.date >= ?1 and m.date <= ?2 and m.driver_id = ?3", nativeQuery = true)
    List<Consumption> findByPeriod(String dateStart, String dateEnd, Integer driver);

//    @Query("SELECT MONTH(m.date) as month, YEAR(m.date) as year, m.type, sum(m.volume) as total_volume, sum(m.price*m.volume) as total_cost, (sum((m.price*m.volume))/sum(m.volume)) as average_price FROM CONSUMPTION m group by MONTH(m.date), YEAR(m.date), m.type")
//    List<GroupedStatsDto> findGroupedStats();
}
