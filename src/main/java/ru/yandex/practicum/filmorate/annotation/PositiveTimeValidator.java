package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveTimeValidator implements ConstraintValidator<PositiveTime, Integer> {
    @Override
    public void initialize(PositiveTime constraintAnnotation) {

    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {

        return value != null && value > 0;
    }
}
