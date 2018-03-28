package pl.droidsonroids.jspoon;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.droidsonroids.jspoon.exception.BigDecimalParseException;
import pl.droidsonroids.jspoon.exception.DateParseException;
import pl.droidsonroids.jspoon.exception.DoubleParseException;
import pl.droidsonroids.jspoon.exception.FieldSetException;
import pl.droidsonroids.jspoon.exception.FloatParseException;

abstract class HtmlField<T> {

    protected final FieldType field;
    protected final SelectorSpec spec;

    HtmlField(FieldType field, SelectorSpec spec) {
        this.field = field;
        this.spec = spec;
    }

    protected abstract void setValue(Jspoon jspoon, Element node, T newInstance);

    protected Elements selectChildren(Element node) {
        return node.select(spec.getCssQuery());
    }

    protected Element selectChild(Element parent) {
        Elements elements = selectChildren(parent);
        int size = elements.size();
        if (size == 0 || size <= spec.getIndex()) {
            return null;
        }
        return elements.get(spec.getIndex());
    }

    static void setFieldOrThrow(FieldType field, Object newInstance, Object value) {
        if (value == null) {
            return;
        }
        try {
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

        try {

            if (fieldType.isAssignableFrom(String.class)) {
                return fieldType.cast(value);
            }

            if (fieldType.equals(Boolean.class)) {
                return fieldType.cast(Boolean.valueOf(value));
            }

            if (fieldType.equals(Integer.class)) {
                return fieldType.cast(Integer.valueOf(value));
            }

            if (fieldType.equals(Long.class)) {
                return fieldType.cast(Long.valueOf(value));
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

        } catch (Throwable t) {

            if (spec.shouldSkipOn(t)) {
                return null;
            }
            throw t;
        }

        // unsupported field type
        // or String field but selected Element does not exist and no set defValue
        return null;
    }

    private <U> String getValue(Element node, Class<U> fieldType) {
        if (node == null) {
            return spec.getDefaultValue();
        }
        String value;
        switch (spec.getAttribute()) {
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
            value = node.attr(spec.getAttribute());
            break;
        }
        if (spec.getRegex() != null) {
            Pattern pattern = Pattern.compile(spec.getRegex());
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                value = (matcher.groupCount() > 0) ? matcher.group(1) : spec.getDefaultValue();
                if (value == null || value.isEmpty()) {
                    value = spec.getDefaultValue();
                }
            }
        }
        return value;
    }

    private Date getDate(String value) {
        try {
            if (spec.getFormat() == null) {
                return DateFormat.getDateInstance(DateFormat.DEFAULT, spec.getLocale()).parse(value);
            }
            return new SimpleDateFormat(spec.getFormat(), spec.getLocale()).parse(value);
        }
        catch (ParseException e) {
            throw new DateParseException(value, spec.getFormat(), spec.getLocale());
        }
    }

    private BigDecimal getBigDecimal(String value) {
        try {
            DecimalFormat decimalFormat = (spec.getFormat() == null)
                    ? (DecimalFormat) DecimalFormat.getInstance(spec.getLocale())
                            : new DecimalFormat(spec.getFormat());

            decimalFormat.setParseBigDecimal(true);
            return (BigDecimal) decimalFormat.parse(value);
        }
        catch (ParseException e) {
            throw new BigDecimalParseException(value, spec.getFormat(), spec.getLocale());
        }

    }

    private Double getDouble(String value) {
        try {
            return NumberFormat.getInstance(spec.getLocale()).parse(value).doubleValue();
        }
        catch (ParseException e) {
            throw new DoubleParseException(value, spec.getLocale());
        }
    }

    private Float getFloat(String value) {
        try {
            return Float.valueOf(NumberFormat.getInstance(spec.getLocale()).parse(value).floatValue());
        }
        catch (ParseException e) {
            throw new FloatParseException(value, spec.getLocale());
        }
    }
}