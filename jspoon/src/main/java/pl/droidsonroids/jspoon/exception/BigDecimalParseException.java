package pl.droidsonroids.jspoon.exception;

import java.util.Locale;

public class BigDecimalParseException extends RuntimeException {
    private static final long serialVersionUID = 3295320880915491873L;

    public BigDecimalParseException(String value, String format, Locale locale) {
        super(String.format(Locale.ENGLISH, "Cannot parse BigDecimal %s with format: %s and locale: %s.", value, format, locale.toLanguageTag()));
    }
}
