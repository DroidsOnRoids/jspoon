package pl.droidsonroids.jspoon.exception;

import java.util.Locale;

public class ObjectCreationException extends RuntimeException {
    private static final long serialVersionUID = -3960224299384172908L;

    public ObjectCreationException(String className) {
        super(String.format(Locale.ENGLISH, "Error while creating instance of %s.", className));
    }
}
