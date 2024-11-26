package id.co.bni.cardbinding.exception.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringToDateValidator implements ConstraintValidator<StringToDate, String> {
    @Override
    public void initialize(StringToDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.isEmpty()){
            return true;
        }
        String REGEX = "^(\\d{4}-\\d{2}-\\d{2}|)$";
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
