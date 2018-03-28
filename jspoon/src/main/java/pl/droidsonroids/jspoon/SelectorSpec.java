package pl.droidsonroids.jspoon;

import static pl.droidsonroids.jspoon.annotation.Selector.NO_VALUE;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import pl.droidsonroids.jspoon.annotation.Format;
import pl.droidsonroids.jspoon.annotation.Selector;
import pl.droidsonroids.jspoon.annotation.SkipOn;

/**
 * Class holding all the required meta-data about a specific {@link Selector} annotated field.
 */
public class SelectorSpec {

    private final String cssQuery;
    private final String attribute;
    private final String defaultValue;
    private final int index;
    private String regex;
    private String format;
    private Locale locale;
    private Class<ElementConverter<?>> converter;
    private Selector selector;
    private Annotation[] annotations;

    @SuppressWarnings("deprecation")
    SelectorSpec(Selector selector, FieldType field) {
        this.selector = selector;
        this.cssQuery = selector.value();
        this.attribute = selector.attr();
        this.defaultValue = NO_VALUE.equals(selector.defValue()) ? null : selector.defValue();
        this.index = selector.index();
        this.annotations = field.getDeclaredAnnotations();

        @SuppressWarnings("unchecked")
        Class<ElementConverter<?>> elConverter = (Class<ElementConverter<?>>) selector.converter();
        this.converter = (!elConverter.isInterface()
                && !Modifier.isAbstract(elConverter.getModifiers()) ? elConverter : null);

        // @Format annotation takes precedence over deprecated attributes
        Format format = field.getAnnotation(Format.class);
        if (format != null && !format.value().trim().isEmpty()) {
            this.format = format.value();
        } else { /* For backwards compatibility */
            if (!NO_VALUE.equals(selector.format())) {
                if (field.isAssignableTo(Date.class) || field.isAssignableTo(BigDecimal.class)) {
                    this.format = selector.format();
                } else {
                    this.regex = selector.format();
                }
            }
        }

        if (format != null && !format.languageTag().trim().isEmpty()) {
            this.locale = Locale.forLanguageTag(format.languageTag());
        } else if (!NO_VALUE.equals(selector.locale())) { /* For backwards compatibility */
            this.locale = Locale.forLanguageTag(selector.locale());
        } else {
            this.locale = Locale.getDefault();
        }

        // New attribute takes precedence if set
        if (!selector.regex().trim().isEmpty()) {
            this.regex = selector.regex();
        }
    }

    Selector getSelectorAnnotation() {
        return this.selector;
    }

    boolean shouldSkipOn(Throwable conversionException) {
        SkipOn skipAnnotation = getDeclaredAnnotation(SkipOn.class);
        if (conversionException != null && skipAnnotation != null
                && skipAnnotation.value() != null) {

            for (Class<? extends Throwable> trowableToSkip : skipAnnotation.value()) {
                if (trowableToSkip.isAssignableFrom(conversionException.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns {@link Selector} field's declared annotation of a particular class.
     * @param <T> annotation class type
     * @param annotationClass the annotation class
     * @return declared annotation or null
     */
    public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        if (annotationClass == null || annotations == null || annotations.length == 0) {
            return null;
        }
        for (Annotation ann : annotations) {
            if (ann.annotationType() == annotationClass) {
                @SuppressWarnings("unchecked")
                T found = (T) ann;
                return found;
            }
        }
        return null;
    }

    /**
     * @return specified CSS query value
     */
    public String getCssQuery() {
        return cssQuery;
    }

    /**
     * @return specified attribute to be selected
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @return specified default value or null
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return element's index in the selected collection
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return returns specified regular expression or null
     */
    public String getRegex() {
        return regex;
    }

    /**
     * @return returns specified expected format to parse or null
     */
    public String getFormat() {
        return format;
    }

    /**
     * @return returns specified locale to be used by a {@link Format} or null
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @return a specified converter to use or null
     */
    public Class<ElementConverter<?>> getConverter() {
        return this.converter;
    }
}