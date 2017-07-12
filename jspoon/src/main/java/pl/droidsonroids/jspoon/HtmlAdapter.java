package pl.droidsonroids.jspoon;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import pl.droidsonroids.jspoon.annotation.Selector;
import pl.droidsonroids.jspoon.exception.ConstrucorNotFoundException;
import pl.droidsonroids.jspoon.exception.ObjectCreationException;

/**
 * Converts HTML strings to Java.
 */
public class HtmlAdapter<T> {
    private Jspoon jspoon;
    private Class<T> clazz;
    private Map<String, HtmlField<T>> htmlFieldCache;

    HtmlAdapter(Jspoon jspoon, Class<T> clazz) {
        this.jspoon = jspoon;
        this.clazz = clazz;
        htmlFieldCache = new LinkedHashMap<>();

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            Class<?> fieldClass = field.getType();

            // Annotated field
            Selector selector = field.getAnnotation(Selector.class);

            // Not annotated field of annotated class
            if (selector == null) {
                selector = fieldClass.getAnnotation(Selector.class);
            }

            // Not annotated field - List of annotated type
            if (selector == null && fieldClass.equals(List.class)) {
                selector = getSelectorFromListType(field);
            }

            if (selector != null) {
                addCachedHtmlField(field, selector, fieldClass);
            }
        }
    }

    /** Converts html string to {@code T} object. **/
    public T fromHtml(String htmlContent) {
        Element pageRoot = Jsoup.parse(htmlContent);
        return loadFromNode(pageRoot);
    }

    private Selector getSelectorFromListType(Field field) {
        Type genericType = field.getGenericType();
        Class<?> listClass = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        return listClass.getAnnotation(Selector.class);
    }

    private void addCachedHtmlField(Field field, Selector selector, Class<?> fieldClass) {
        HtmlField<T> htmlField;
        if (fieldClass.equals(List.class)) {
            htmlField = new HtmlListField<>(field, selector);
        } else if (Utils.isSimple(fieldClass)) {
            htmlField = new HtmlSimpleField<>(field, selector);
        } else {
            htmlField = new HtmlClassField<>(field, selector);
        }
        htmlFieldCache.put(field.getName(), htmlField);
    }

    T loadFromNode(Element node) {
        T newInstance = createNewInstance();
        for (HtmlField<T> htmlField : htmlFieldCache.values()) {
            htmlField.setValue(jspoon, node, newInstance);
        }
        return newInstance;
    }

    private T createNewInstance() {
        T newInstance;
        Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new ConstrucorNotFoundException(clazz.getSimpleName());
        }
        constructor.setAccessible(true);
        try {
            newInstance = constructor.newInstance();
        } catch (Exception e) {
            throw new ObjectCreationException(clazz.getSimpleName());
        }
        return newInstance;
    }
}
