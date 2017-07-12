package pl.droidsonroids.jspoon;

import java.lang.reflect.Field;
import java.text.DateFormat;
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
import pl.droidsonroids.jspoon.exception.DateParseException;
import pl.droidsonroids.jspoon.exception.FieldSetException;
import pl.droidsonroids.jspoon.exception.FloatParseException;

abstract class HtmlField<T> {
    Field field;
    Selector selector;

    HtmlField(Field field, Selector selector) {
        this.field = field;
        this.selector = selector;
    }

    public abstract void setValue(Jspoon jspoon, Element node, T newInstance);

    Element getSelectedNode(Element node) {
        String cssQuery = selector.value();
        int index = selector.index();
        return getAtIndexOrNull(node, cssQuery, index);
    }

    private static Element getAtIndexOrNull(Element node, String cssQuery, int index) {
        Elements elements = node.select(cssQuery);
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
    <U> U instanceForNode(Element node, Class<U> clazz) {
        String attribute = selector.attr();
        String format = selector.format();
        String locale = selector.locale();
        String defValue = selector.defValue();

        if (clazz.equals(Element.class)) {
            return (U) node;
        }
        String value = getValue(node, clazz, attribute, format, defValue);

        if (clazz.equals(String.class)) {
            return (U) value;
        }

        if (clazz.equals(Integer.class) || clazz.getSimpleName().equals("int")) {
            return (U) Integer.valueOf(value);
        }

        if (clazz.equals(Boolean.class) || clazz.getSimpleName().equals("boolean")) {
            return (U) Boolean.valueOf(value);
        }

        if (clazz.equals(Date.class)) {
            Locale loc = Locale.getDefault();
            if (!locale.equals(Selector.NO_VALUE)) {
                loc = Locale.forLanguageTag(locale);
            }
            DateFormat dateFormat = new SimpleDateFormat(format, loc);
            try {
                return (U) dateFormat.parse(value);
            } catch (ParseException e) {
                throw new DateParseException(value, format, locale);
            }
        }

        if (clazz.equals(Float.class) || clazz.getSimpleName().equals("float")) {
            if (!locale.equals(Selector.NO_VALUE)) {
                Locale loc = Locale.forLanguageTag(locale);
                NumberFormat numberFormat = NumberFormat.getInstance(loc);
                Number number;
                try {
                    number = numberFormat.parse(value);
                } catch (ParseException e) {
                    throw new FloatParseException(value, locale);
                }
                return (U) Float.valueOf(number.floatValue());
            } else {
                return (U) Float.valueOf(value);
            }
        }

        return (U) value;
    }

    private <U> String getValue(Element node, Class<U> clazz, String attribute, String format, String defValue) {
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
        if (!clazz.equals(Date.class) && !format.equals(Selector.NO_VALUE)) {
            Pattern pattern = Pattern.compile(format);
            Matcher matcher = pattern.matcher(value);
            boolean found = matcher.find();
            if (found) {
                value = matcher.group(1);
                if (value.isEmpty()) {
                    value = defValue;
                }
            } else {
                value = defValue;
            }
        }
        return value;
    }
}
