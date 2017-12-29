package pl.droidsonroids.jspoon.exception;

/**
 * Exception thrown when type doesn't contain a field, or is not annotated with {@code @Selector}
 */
public class EmptySelectorException extends RuntimeException {

    public EmptySelectorException(Class type) {
        super(String.format("Unable to find @Selector on type '%s', or its fields. "
            + "Is this type intended for parsing HTML?", type));
    }
}
