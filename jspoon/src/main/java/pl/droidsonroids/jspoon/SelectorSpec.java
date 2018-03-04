package pl.droidsonroids.jspoon;

import static pl.droidsonroids.jspoon.annotation.Selector.NO_VALUE;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import pl.droidsonroids.jspoon.annotation.Format;
import pl.droidsonroids.jspoon.annotation.Nullable;
import pl.droidsonroids.jspoon.annotation.Selector;

public class SelectorSpec {

    private final String cssQuery;
    private final String attribute;
    private final String defaultValue;
    private final int index;
    private String regex;
    private String format;
    private Locale locale;
    private boolean nullable;
    private Class<ElementConverter<?>> converter;
    private Selector selector;

    @SuppressWarnings("deprecation")
    SelectorSpec(Selector selector, FieldType field) {
        this.selector = selector;
        this.cssQuery = selector.value();
        this.attribute = selector.attr();
        this.defaultValue = NO_VALUE.equals(selector.defValue()) ? null : selector.defValue();
        this.index = selector.index();
        this.nullable = (field.getAnnotation(Nullable.class) != null);

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

    public String getCssQuery() {
        return cssQuery;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public int getIndex() {
        return index;
    }

    public String getRegex() {
        return regex;
    }

    public String getFormat() {
        return format;
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Class<ElementConverter<?>> getConverter() {
        return this.converter;
    }
}