package pl.droidsonroids.jspoon.exception;

import java.util.Locale;

public class DateParseException extends RuntimeException {
    private static final long serialVersionUID = -3960224299384172906L;

    public DateParseException(String value, String format, String locale) {
        super(String.format(Locale.ENGLISH, "Cannot parse date %s with format: %s and locale: %s.", value, format, locale));
    }
}
