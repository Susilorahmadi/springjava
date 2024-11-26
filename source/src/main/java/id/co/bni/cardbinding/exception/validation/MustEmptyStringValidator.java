package id.co.bni.cardbinding.exception.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MustEmptyStringValidator implements ConstraintValidator<MustEmptyString, String> {
    @Override
    public void initialize(MustEmptyString constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        return value != null && value.isEmpty();
    }
}
