package id.co.bni.cardbinding.exception.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String REGEX = "^628\\d+$";
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(value);

        return matcher.matches();
    }
}
