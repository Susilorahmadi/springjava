package id.co.bni.cardbinding.exception.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveNumberValidator implements ConstraintValidator<PositiveNumber, String> {

    @Override
    public void initialize(PositiveNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.isEmpty()){
            return true;
        }
        try {
            return Integer.parseInt(value) > 0;
        } catch (Exception ex){
            return false;
        }
    }
}