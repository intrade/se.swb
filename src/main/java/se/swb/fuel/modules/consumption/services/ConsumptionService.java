package se.swb.fuel.modules.consumption.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;
import se.swb.fuel.modules.consumption.dto.ConsumptionDTO;
import se.swb.fuel.modules.consumption.dto.FormDTO;
import se.swb.fuel.modules.consumption.dto.FuelCostDTO;
import se.swb.fuel.modules.consumption.dto.GroupedStatsDto;
import se.swb.fuel.modules.consumption.models.Consumption;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;

public interface ConsumptionService {
    List<Consumption> listConsumptions();
    List<FuelCostDTO> getMonthlyStatistic(Year fromYear);
    List<FuelCostDTO> getMonthlyStatistic(Year fromYear, Month fromMonth);

    List<ConsumptionDTO> listConsumptionsFor(LocalDate date, Optional<Integer> driver);

    List<GroupedStatsDto> getGroupedStats(Optional<Integer> driver);

    ConsumptionDTO store(FormDTO consumption);

    void parseFile(MultipartFile file) throws FileUploadException;
}
