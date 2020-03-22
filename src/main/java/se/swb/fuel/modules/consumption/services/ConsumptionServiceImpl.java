package se.swb.fuel.modules.consumption.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.swb.fuel.modules.consumption.converters.ConsumptionToDto;
import se.swb.fuel.modules.consumption.dto.ConsumptionDTO;
import se.swb.fuel.modules.consumption.dto.FormDTO;
import se.swb.fuel.modules.consumption.dto.FuelCostDTO;
import se.swb.fuel.modules.consumption.dto.GroupedStatsDto;
import se.swb.fuel.modules.consumption.models.Consumption;
import se.swb.fuel.modules.consumption.models.FuelType;
import se.swb.fuel.modules.consumption.repositories.ConsumptionJpaRepository;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConsumptionServiceImpl implements ConsumptionService {
    @Autowired
    private ConsumptionJpaRepository consumptionRepository;
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Consumption> listConsumptions() {
        List<Consumption> list = consumptionRepository.findAll();
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public List<FuelCostDTO> getMonthlyStatistic(Year fromYear, Month fromMonth) {
        LocalDate date = LocalDate.of(fromYear.getValue(), fromMonth, 1);
        List<Consumption> consumptions = consumptionRepository.findAllFromDate(date.toString());
        Map<Integer, Map<Integer, BigDecimal>> consumtionTree = new HashMap<>();
        consumptions.stream()
                .forEach(c -> {
                    if (!consumtionTree.containsKey(c.getDate().getYear())) {
                        Map<Integer, BigDecimal> m = new HashMap<>();
                        m.put(c.getDate().getMonthValue(),
                                c.getPrice().multiply(BigDecimal.valueOf(c.getVolume())));
                        consumtionTree.put(c.getDate().getYear(), m);
                    } else {
                        Map<Integer, BigDecimal> statsForYear = consumtionTree.get(c.getDate().getYear());
                        if (!statsForYear.containsKey(c.getDate().getMonthValue())){
                            statsForYear.put(c.getDate().getMonthValue(), c.getPrice().multiply(BigDecimal.valueOf(c.getVolume())));
                        } else {
                            BigDecimal cons = statsForYear.get(c.getDate().getMonthValue());
                            statsForYear.replace(
                                    c.getDate().getMonthValue(),
                                    cons,
                                    cons.add(c.getPrice().multiply(BigDecimal.valueOf(c.getVolume())))
                            );
                        }
                    }

                });
        Set<Integer> years = consumtionTree.keySet();
        List<FuelCostDTO> dtos = new ArrayList<>();
        years.stream().forEach(y -> {
            Map<Integer, BigDecimal> months = consumtionTree.get(y);
            months.entrySet().stream().map(e -> new FuelCostDTO(Month.of(e.getKey()), Year.of(y), e.getValue())).forEach(dtos::add);
        });
        return dtos;
    }

    @Override
    public List<ConsumptionDTO> listConsumptionsFor(LocalDate date, Optional<Integer> driver) {
        LocalDate end = date.plusMonths(1).minusDays(1);
        List<Consumption> consumptions;
        if (!driver.isPresent()) {
            consumptions = consumptionRepository.findByPeriod(date.toString(), end.toString());
        } else {
            consumptions = consumptionRepository.findByPeriod(date.toString(), end.toString(), driver.get());
        }
        List<ConsumptionDTO> dtos = consumptions.stream().map(ConsumptionToDto::convert).collect(Collectors.toList());
        return dtos;
    }

    @Override
    public List<GroupedStatsDto> getGroupedStats(Optional<Integer> driver) {
        List<GroupedStatsDto> statsDtos;
        if (!driver.isPresent()) {
            statsDtos = entityManager.createNamedQuery("groupedStatsDto").getResultList();
        } else {
            statsDtos = entityManager.createNamedQuery("paramGroupedStatsDto").setParameter("driver", driver.get()).getResultList();
        }
        return statsDtos;
    }

    @Override
    public ConsumptionDTO store(FormDTO consumption) {
        Consumption cons = new Consumption();
        cons.setDate(LocalDate.parse(consumption.getDate()));
        cons.setDriverId(consumption.getDriverId());
        cons.setPrice(BigDecimal.valueOf(consumption.getPrice()));
        cons.setVolume(consumption.getVolume());
        cons.setType(FuelType.valueOf(consumption.getType()));
        cons = consumptionRepository.saveAndFlush(cons);
        return ConsumptionToDto.convert(cons);
    }

    @Override
    public void parseFile(MultipartFile file) throws FileUploadException {
        try ( BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){
            List<Consumption> consumptionList = new ArrayList<>();
            boolean hasErrors = false;
            String errorMessage = "";
            String line;
            int i = 1;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                if (
                        !values[0].matches("(E95|E98|D)") ||
                        LocalDate.parse(values[1], DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                .atStartOfDay()
                                .isAfter(LocalDate.now().atStartOfDay()) ||
                                Double.parseDouble(values[2])<0.01 ||
                                Double.parseDouble(values[3])<0.1 ||
                                Integer.parseInt(values[4])<1
                ) {
                    hasErrors = true;
                    errorMessage = "Your file has error on line #"+i;
                }

                if (hasErrors) throw new FileUploadException(errorMessage);

                Consumption c = new Consumption(values[0], values[1], values[2], values[3], values[4]);
                consumptionList.add(c);

            }

            consumptionList.stream()
                    .forEach(consumptionRepository::saveAndFlush);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<FuelCostDTO> getMonthlyStatistic(Year fromYear) {
        return this.getMonthlyStatistic(fromYear, Month.JANUARY);
    }
}
