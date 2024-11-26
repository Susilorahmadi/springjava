package id.co.bni.cardbinding.exception.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsPayValidator implements ConstraintValidator<IsPay, String> {

    @Override
    public void initialize(IsPay constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && (value.equals("Y") || value.equals("N"));
    }
}