package pl.droidsonroids.jspoon.exception;

import java.util.Locale;

public class DoubleParseException extends RuntimeException {
    private static final long serialVersionUID = -3960224299384172912L;

    public DoubleParseException(String value, Locale locale) {
        super(String.format(Locale.ENGLISH, "Cannot parse double %s with locale: %s.", value, locale));
    }
}
