package pl.droidsonroids.jspoon.exception;

import java.util.Locale;

public class FieldSetException extends RuntimeException {
    private static final long serialVersionUID = -3960224299384172910L;

    public FieldSetException(String className, String field) {
        super(String.format(Locale.ENGLISH, "Error while setting %s in %s object", field, className));
    }
}
