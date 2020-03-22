package se.swb.fuel.modules.consumption.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import se.swb.fuel.modules.consumption.dto.FormDTO;

import java.time.LocalDate;

public class FormDataValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return FormDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FormDTO dto = (FormDTO) target;
        if (dto.getDriverId() < 1) errors.rejectValue("driverId", "Driver ID can't be lower than 1. Your value is: "+dto.getDriverId());
        if (LocalDate.parse(dto.getDate()).isAfter(LocalDate.now())) errors.rejectValue("date", "Date can't be from the future!");
        if (!dto.getType().matches("(D|E95|E98)")) errors.rejectValue("type", "Unsupported fuel type. Please specify [D]isel, E95 or E98");
        if (dto.getVolume() < 0) errors.rejectValue("volume", "Fuel volume can not be negative value. You provide: "+dto.getVolume());
        if (dto.getPrice() < 0) errors.rejectValue("price", "Price can not be negative value");
    }
}
