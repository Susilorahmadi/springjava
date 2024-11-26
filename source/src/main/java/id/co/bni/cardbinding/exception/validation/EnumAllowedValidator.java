package id.co.bni.cardbinding.exception.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class EnumAllowedValidator implements ConstraintValidator<EnumAllowed, String> {

    private List<String> allowedValues;
    private String message;

    @Override
    public void initialize(EnumAllowed constraintAnnotation) {
        allowedValues = Arrays.asList(constraintAnnotation.allowedValues());
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || !allowedValues.contains(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}