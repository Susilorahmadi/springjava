package id.co.bni.cardbinding.exception.validation;

import java.util.regex.Pattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AntiXSSValidator implements ConstraintValidator<AntiXXS, String> {

    private static final Pattern[] XSS_PATTERNS = {
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };

    // Regular expressions to detect common SQL injection patterns
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
            Pattern.compile("('.+--)|(--)|(%7C)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("((\\%27)|('))union", Pattern.CASE_INSENSITIVE),
            Pattern.compile("exec(\\s|\\+)+(s|x)p\\w+", Pattern.CASE_INSENSITIVE),
            Pattern.compile("select(\\s|\\+)+.+(\\s|\\+)+from", Pattern.CASE_INSENSITIVE),
            Pattern.compile("insert(\\s|\\+)+into", Pattern.CASE_INSENSITIVE),
            Pattern.compile("delete(\\s|\\+)+from", Pattern.CASE_INSENSITIVE),
            Pattern.compile("drop(\\s|\\+)+table", Pattern.CASE_INSENSITIVE),
            Pattern.compile("update(\\s|\\+)+.+(\\s|\\+)+set", Pattern.CASE_INSENSITIVE),
            Pattern.compile("alter(\\s|\\+)+table", Pattern.CASE_INSENSITIVE)
    };

    @Override
    public void initialize(AntiXXS constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.isEmpty()){
            return true;
        }
        try {
            if (value != null) {
                for (Pattern pattern : XSS_PATTERNS) {
                    if (pattern.matcher(value).find()) {
                        return false;
                    }
                }
            }

            if (value != null) {
                for (Pattern pattern : SQL_INJECTION_PATTERNS) {
                    if (pattern.matcher(value).find()) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception ex){
            return false;
        }
    }
}