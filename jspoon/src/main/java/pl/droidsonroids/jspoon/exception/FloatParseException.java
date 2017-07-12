package pl.droidsonroids.jspoon.exception;

import java.util.Locale;

public class FloatParseException extends RuntimeException {
    private static final long serialVersionUID = -3960224299384172907L;

    public FloatParseException(String value, String locale) {
        super(String.format(Locale.ENGLISH, "Cannot parse float %s with locale: %s.", value, locale));
    }
}
