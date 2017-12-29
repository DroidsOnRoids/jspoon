package pl.droidsonroids.jspoon.exception;

public class ConstructorNotFoundException extends ConstrucorNotFoundException {
    private static final long serialVersionUID = -9106503492095273112L;

    public ConstructorNotFoundException(String className) {
        super(className);
    }
}
