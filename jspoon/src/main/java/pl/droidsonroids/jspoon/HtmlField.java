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
import pl.droidsonroids.jspoon.exception.FieldSetException;
import pl.droidsonroids.jspoon.exception.FloatParseException;

abstract class HtmlField<T> {

    protected Field field;
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

    protected abstract void setValue(Jspoon jspoon, Element node, T newInstance);

    protected Elements selectChildren(Element node) {
        return node.select(cssQuery);
    }

    protected Element selectChild(Element parent) {
        Elements elements = selectChildren(parent);
        int size = elements.size();
        if (size == 0 || size <= index) {
            return null;
        }
        return elements.get(index);
    }

    static void setFieldOrThrow(Field field, Object newInstance, Object value) {
        if (value == null || Selector.NO_VALUE.equals(value)){
            return;
        }
        try {
            field.setAccessible(true);
            field.set(newInstance, value);
        }
        catch (IllegalAccessException e) {
            throw new FieldSetException(newInstance.getClass().getSimpleName(), field.getName());
        }
    }

    protected <U> U instanceForNode(Element node, Class<U> fieldType) {
        // if clazz.isPrimitive convert it to it's Object counterpart
        fieldType = Utils.wrapToObject(fieldType);

        if (fieldType.isAssignableFrom(Element.class)) { // allow Element's super classes like Node as well
            return fieldType.cast(node);
        }

        String value = getValue(node, fieldType);

        if (fieldType.equals(String.class)) {
            return fieldType.cast(value);
        }

        if (fieldType.equals(Integer.class)) {
            return fieldType.cast(Integer.valueOf(value));
        }

        if (fieldType.equals(Long.class)) {
            return fieldType.cast(Long.valueOf(value));
        }

        if (fieldType.equals(Boolean.class)) {
            return fieldType.cast(Boolean.valueOf(value));
        }

        if (fieldType.equals(Date.class)) {
            return fieldType.cast(getDate(value));
        }

        if (fieldType.equals(Float.class)) {
            return fieldType.cast(getFloat(value));
        }

        if (fieldType.equals(Double.class)) {
            return fieldType.cast(getDouble(value));
        }

        if (fieldType.equals(BigDecimal.class)) {
            return fieldType.cast(getBigDecimal(value));
        }

        // unsupported field type
        // or String field but selected Element does not exist and no set defValue
        return null;
    }

    private <U> String getValue(Element node, Class<U> fieldType) {
        if (node == null) {
            return defValue;
        }
        String value;
        switch (attribute) {
        case "":
        case "text":
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
        if (!fieldType.equals(Date.class) && !fieldType.equals(BigDecimal.class)
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

    private Date getDate(String value) {
        try {
            if (Selector.NO_VALUE.equals(format)) {
                return DateFormat.getDateInstance(DateFormat.DEFAULT, locale).parse(value);
            }
            return new SimpleDateFormat(format, locale).parse(value);
        }
        catch (ParseException e) {
            throw new DateParseException(value, format, locale);
        }
    }

    private BigDecimal getBigDecimal(String value) {
        try {
            DecimalFormat decimalFormat = Selector.NO_VALUE.equals(format)
                    ? (DecimalFormat) DecimalFormat.getInstance(locale) : new DecimalFormat(format);

            decimalFormat.setParseBigDecimal(true);
            return (BigDecimal) decimalFormat.parse(value);
        }
        catch (ParseException e) {
            throw new BigDecimalParseException(value, format, locale);
        }

    }

    private Double getDouble(String value) {
        try {
            return NumberFormat.getInstance(locale).parse(value).doubleValue();
        }
        catch (ParseException e) {
            throw new DateParseException(value, format, locale);
        }
    }

    private Float getFloat(String value) {
        try {
            return Float.valueOf(NumberFormat.getInstance(locale).parse(value).floatValue());
        }
        catch (ParseException e) {
            throw new FloatParseException(value, locale);
        }
    }
}