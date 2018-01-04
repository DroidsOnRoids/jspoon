package pl.droidsonroids.jspoon;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import pl.droidsonroids.jspoon.annotation.Selector;
import pl.droidsonroids.jspoon.exception.ConstructorNotFoundException;
import pl.droidsonroids.jspoon.exception.EmptySelectorException;
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
            if (selector == null && List.class.isAssignableFrom(fieldClass)) {
                selector = getSelectorFromListType(field);
            }

            if (selector != null) {
                addCachedHtmlField(field, selector, fieldClass);
            }
        }

        if (htmlFieldCache.isEmpty()) {
            throw new EmptySelectorException( clazz );
        }
    }

    /**
     * Converts html string to {@code T} object.
     * @param htmlContent String with HTML content
     * @return Created object
     */
    public T fromHtml(String htmlContent) {
        Element pageRoot = Jsoup.parse(htmlContent);
        return loadFromNode(pageRoot);
    }

    /**
     * Converts the provided {@code InputStream} to a {@code T} object.
     * <p>
     * Does not close the {@code InputStream}.
     *
     * @param inputStream InputStream with HTML content
     * @return Created object of type {@code T}
     * @throws IOException If I/O error occurs while reading the {@code InputStream}
     */
    public T fromInputStream(InputStream inputStream) throws IOException {
        return fromInputStream(inputStream, null);
    }

    /**
     * Converts the provided {@code inputStream} to a {@code T} object.
     * <p>
     * Does not close the {@code InputStream}.
     *
     * @param inputStream InputStream with HTML content
     * @param baseUrl The URL where the HTML was retrieved from, to resolve relative links against.
     * @return Created object of type {@code T}
     * @throws IOException If I/O error occurs while reading the {@code InputStream}
     */
    public T fromInputStream(InputStream inputStream, URL baseUrl) throws IOException {
        return fromInputStream(inputStream, null, baseUrl);
    }

    /**
     * Converts the provided {@code inputStream} to a {@code T} object.
     * <p>
     * Does not close the {@code InputStream}.
     *
     * @param inputStream InputStream with HTML content
     * @param charset Charset to use
     * @param baseUrl The URL where the HTML was retrieved from, to resolve relative links against.
     * @return Created object of type {@code T}
     * @throws IOException If I/O error occurs while reading the {@code InputStream}
     */
    public T fromInputStream(InputStream inputStream, Charset charset, URL baseUrl) throws IOException {
        String urlToUse = baseUrl != null ? baseUrl.toString() : null;
        String charsetToUse = charset != null ? charset.name() : null;
        Element root = Jsoup.parse(inputStream, charsetToUse, urlToUse);
        return loadFromNode(root);
    }

    private Selector getSelectorFromListType(Field field) {
        Type genericType = field.getGenericType();
        Class<?> listClass = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        return listClass.getAnnotation(Selector.class);
    }

    private void addCachedHtmlField(Field field, Selector selector, Class<?> fieldClass) {
        HtmlField<T> htmlField;
        if (List.class.isAssignableFrom(fieldClass)) {
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
            throw new ConstructorNotFoundException(clazz.getSimpleName());
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
