package com.unicine.util.validation.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class MultiPatternValidator implements ConstraintValidator<MultiPattern, Object> {

    private Pattern[] patterns;

    @Override
    public void initialize(MultiPattern constraintAnnotation) {
        this.patterns = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Considera null como válido, usa @NotNull para validar null
        }

        if (value instanceof String) {
            return validateString((String) value, context);
        } else if (value instanceof List) {
            return validateList((List<?>) value, context);
        }

        return false; // Tipo no soportado
    }

    private boolean validateString(String value, ConstraintValidatorContext context) {
        for (Pattern pattern : patterns) {
            if (!value.matches(pattern.regexp())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(pattern.message()).addConstraintViolation();
                return false;
            }
        }
        return true;
    }

    private boolean validateList(List<?> value, ConstraintValidatorContext context) {
        for (Object item : value) {
            if (item instanceof String) {
                if (!validateString((String) item, context)) {
                    return false;
                }
            } else {
                return false; // Tipo no soportado en la lista
            }
        }
        return true;
    }
}