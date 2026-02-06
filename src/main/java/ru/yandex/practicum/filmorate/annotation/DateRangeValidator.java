package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<DateRange, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(DateRange constraintAnnotation) {

        this.minDate = LocalDate.parse(constraintAnnotation.minDate(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate today = LocalDate.now();

        return !value.isBefore(minDate) && !value.isAfter(today);
    }
}
