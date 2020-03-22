package se.swb.fuel.modules.consumption.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import se.swb.fuel.modules.consumption.dto.ConsumptionDTO;
import se.swb.fuel.modules.consumption.dto.FormDTO;
import se.swb.fuel.modules.consumption.dto.FuelCostDTO;
import se.swb.fuel.modules.consumption.dto.GroupedStatsDto;
import se.swb.fuel.modules.consumption.models.Consumption;
import se.swb.fuel.modules.consumption.services.ConsumptionService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/api/consumption")
public class ConsumptionController {
    private MethodParameter formDtoMethodParameter;

    @Autowired
    private ConsumptionService consumtionService;

    @PostConstruct
    void init() throws NoSuchMethodException {
        formDtoMethodParameter = new MethodParameter(
                ConsumptionController.class.getMethod(
                        "saveConsumption",
                        FormDTO.class,
                        BindingResult.class), 0 );
    }

    @InitBinder
    void initBinder(WebDataBinder binder){
        binder.setValidator(new FormDataValidator());
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Consumption> getConsumtionHistory(){
        return consumtionService.listConsumptions();
    }

    @RequestMapping(value = {"/detailed/{month}/{year}","/detailed/{driver}/{month}/{year}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConsumptionDTO> getConsumtionHistoryForMonth(
            @PathVariable(name = "month") String month,
            @PathVariable(name = "year") String year,
            @PathVariable(name = "driver", required = false) Optional<Integer> driver
            ) throws HttpClientErrorException {
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        if (date.isAfter(LocalDate.now())) throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Incorrect date requested. Your date is: "+date.toString());
        return consumtionService.listConsumptionsFor(date, driver);
    }

    @RequestMapping(value = "/monthlyStats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FuelCostDTO> getMonthlyFuelCostForCurrentYear() throws HttpClientErrorException {
        return this.getMonthlyFuelCost(String.valueOf(Month.JANUARY.getValue()), Year.now().toString());
    }

    @RequestMapping(value = "/monthlyStats/{fromMonth}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FuelCostDTO> getMonthlyFuelCostForCurrentYearFromMonth(
            @PathVariable(value = "fromMonth") String fromMonth
    ) throws HttpClientErrorException {
        return this.getMonthlyFuelCost(fromMonth, Year.now().toString());
    }

    @RequestMapping(value = "/monthlyStats/{fromMonth}/{fromYear}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FuelCostDTO> getMonthlyFuelCost(
            @PathVariable(value = "fromMonth") String fromMonth,
            @PathVariable(value = "fromYear") String fromYear
    ) throws HttpClientErrorException {
        log.info("Month: "+fromMonth + " Year: "+fromYear);
        if (fromYear != null && Year.parse(fromYear).getValue()>Year.now().getValue()) throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Start year ["+fromYear+"] can't be greater than current");
        if (fromYear == null) fromYear = String.valueOf(Year.now().getValue());
        if (fromMonth != null &&
                Integer.parseInt(fromYear) == Year.now().getValue() &&
                Integer.parseInt(fromMonth) > LocalDate.now().getMonth().getValue()
        ) throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Start date can't be greater than current. Please specify correct month and year");
        if (fromMonth!=null){
            return consumtionService.getMonthlyStatistic(Year.parse(fromYear), Month.of(Integer.parseInt(fromMonth)));
        }
        return consumtionService.getMonthlyStatistic(Year.parse(fromYear));
    }

    @RequestMapping(value = {"/stat","/stat/{driver}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GroupedStatsDto> getStat(@PathVariable(name = "driver", required = false) Optional<Integer> driver){
        return consumtionService.getGroupedStats(driver);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ConsumptionDTO saveConsumption(@Validated @RequestBody FormDTO consumption, BindingResult result) throws MethodArgumentNotValidException {
        if (result.hasErrors()) throw new MethodArgumentNotValidException(formDtoMethodParameter, result);
        return consumtionService.store(consumption);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public List<Consumption> upload(@RequestParam("file") MultipartFile file) throws FileUploadException {
        if (file.isEmpty()) throw new FileUploadException("Your file is empty.");
        if (file.getSize() > 1024*1024) throw new FileUploadException("File is to large.");
        consumtionService.parseFile(file);
        return this.getConsumtionHistory();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> processHandler(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
    }

    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> processHandler(FileUploadException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
