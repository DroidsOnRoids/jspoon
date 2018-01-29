package pl.droidsonroids.jspoon;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.droidsonroids.jspoon.annotation.Selector;
import pl.droidsonroids.jspoon.exception.BigDecimalParseException;
import pl.droidsonroids.jspoon.exception.DateParseException;
import pl.droidsonroids.jspoon.exception.DoubleParseException;
import pl.droidsonroids.jspoon.exception.FieldSetException;
import pl.droidsonroids.jspoon.exception.FloatParseException;

abstract class HtmlField<T> {
    Field field;
    private String cssQuery;
    private String attribute;
    private String format;
    private Locale locale;
    private String defValue;
    private int index;

    HtmlField(Field field, Selector selector) {
        this.field = field;
        cssQuery = selector.value();
        attribute = selector.attr();
        format = selector.format();
        setLocaleFromTag(selector.locale());
        defValue = selector.defValue();
        index = selector.index();
    }

    private void setLocaleFromTag(String localeTag) {
        if (localeTag.equals(Selector.NO_VALUE)) {
            locale = Locale.getDefault();
        } else {
            locale = Locale.forLanguageTag(localeTag);
        }
    }

    public abstract void setValue(Jspoon jspoon, Element node, T newInstance);

    Element selectChild(Element parent) {
        return getElementAtIndexOrNull(parent);
    }

    Elements selectChildren(Element node) {
        return node.select(cssQuery);
    }

    private Element getElementAtIndexOrNull(Element parent) {
        Elements elements = selectChildren(parent);
        int size = elements.size();
        if (size == 0 || size <= index) {
            return null;
        }
        return elements.get(index);
    }

    static void setFieldOrThrow(Field field, Object newInstance, Object value) {
        try {
            field.setAccessible(true);
            field.set(newInstance, value);
        } catch (IllegalAccessException e) {
            throw new FieldSetException(newInstance.getClass().getSimpleName(), field.getName());
        }
    }

    @SuppressWarnings("unchecked")
    <U> U instanceForNode(Element node, Class<U> clazz, T enclosingInstance) {
        if (clazz.equals(Element.class)) {
            return (U) node;
        }
        String value = getValue(node, clazz);

        if (value.equals(defValue)) {
            // check first if the field already has a value assigned
            Object fieldValue = getFieldValue(enclosingInstance, null);
            if (fieldValue != null)
                return (U) fieldValue;
        }

        if (clazz.equals(String.class)) {
            return (U) value;
        }

        if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return (U) Integer.valueOf(value);
        }

        if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            return (U) Long.valueOf(value);
        }

        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
            return (U) Boolean.valueOf(value);
        }

        if (clazz.equals(Date.class)) {
            DateFormat dateFormat = getDateFormat();
            try {
                return (U) dateFormat.parse(value);
            } catch (ParseException e) {
                throw new DateParseException(value, format, locale);
            }
        }

        if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            try {
                Number number = getNumberFromString(value);
                return (U) Float.valueOf(number.floatValue());
            } catch (ParseException e) {
                throw new FloatParseException(value, locale);
            }
        }

        if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            try {
                Number number = getNumberFromString(value);
                return (U) Double.valueOf(number.floatValue());
            } catch (ParseException e) {
                throw new DoubleParseException(value, locale);
            }
        }

        if (clazz.equals(BigDecimal.class)) {
            DecimalFormat decimalFormat = getDecimalFormat();
            try {
                return (U) decimalFormat.parse(value);
            } catch (ParseException e) {
                throw new BigDecimalParseException(value, format, locale);
            }
        }

        return (U) value;
    }

    <U> U getFieldValue(T enclosingInstance, U defaultValue) {
        try {
            field.setAccessible(true);
            Object assignedValue = field.get(enclosingInstance);

            if (field.getType().isPrimitive()) {
                // Primitive types always have a default value, so we check
                // whether the value is different from the jvm default
                Object primitiveDefault = Utils.getDefaultValueForType(field.getType());
                if (assignedValue.equals(primitiveDefault))
                    assignedValue = defaultValue;
            }
            if (assignedValue == null)
                assignedValue = defaultValue;

            return (U) assignedValue;
        } catch (IllegalAccessException e) {
            // do nothing, let the default value from the annotation take over
            return defaultValue;
        }
    }

    private <U> String getValue(Element node, Class<U> clazz) {
        if (node == null) {
            return defValue;
        }
        String value;
        switch (attribute) {
            case "":
                value = node.text();
                break;
            case "html":
            case "innerHtml":
                value = node.html();
                break;
            case "outerHtml":
                value = node.outerHtml();
                break;
            default:
                value = node.attr(attribute);
                break;
        }
        if (!clazz.equals(Date.class)
                && !clazz.equals(BigDecimal.class)
                && !format.equals(Selector.NO_VALUE)) {
            Pattern pattern = Pattern.compile(format);
            Matcher matcher = pattern.matcher(value);
            boolean found = matcher.find();
            if (found) {
                value = matcher.group(1);
                if (value.isEmpty()) {
                    value = defValue;
                }
            }
        }
        return value;
    }

    private DateFormat getDateFormat() {
        if (Selector.NO_VALUE.equals(format)) {
            return DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        } else {
            return new SimpleDateFormat(format, locale);
        }
    }

    private DecimalFormat getDecimalFormat() {
        DecimalFormat instance;
        if (Selector.NO_VALUE.equals(format)) {
            instance = (DecimalFormat) DecimalFormat.getInstance(locale);
        } else {
            instance = new DecimalFormat(format);
        }
        instance.setParseBigDecimal(true);
        return instance;
    }

    private Number getNumberFromString(String value) throws ParseException {
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        return numberFormat.parse(value);
    }
}
