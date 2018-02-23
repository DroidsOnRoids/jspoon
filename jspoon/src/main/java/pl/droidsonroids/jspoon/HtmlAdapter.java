package pl.droidsonroids.jspoon;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import pl.droidsonroids.jspoon.annotation.Selector;
import pl.droidsonroids.jspoon.exception.EmptySelectorException;

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

        for (Field f : Utils.getAllDeclaredFields(clazz)) {

            FieldType field = new FieldType(clazz, f);

            if (!field.isModifiable()) {
                continue;
            }

            // Annotated field
            Selector selector = field.getAnnotation(Selector.class);

            // Not annotated field of annotated class
            if (selector == null) {
                selector = field.getType().getAnnotation(Selector.class);
            }

            // Not annotated field - List of annotated type
            if (selector == null && isCollectionLike(field)) {
                selector = getFromComponentType(field);
            }

            if (selector != null) {
                addCachedHtmlField(field, selector);
            }
        }

        if (htmlFieldCache.isEmpty()) {
            throw new EmptySelectorException(clazz);
        }
    }

    private Selector getFromComponentType(FieldType field) {
        if (field.isArray()) {
            return field.getArrayContentType().getAnnotation(Selector.class);
        }
        if (field.getTypeArgumentCount() == 1) {
            return field.getTypeArgument(0).getAnnotation(Selector.class);
        }
        return null;
    }

    /**
     * Populates given {@code T} instance from html string.
     *
     * @param htmlContent String with HTML content
     * @param instance instance to populate
     * @return Created object
     */
    public T fromHtml(String htmlContent, T instance) {
        Element pageRoot = Jsoup.parse(htmlContent);
        return loadFromNode(pageRoot, instance);
    }

    /**
     * Converts html string to {@code T} object.
     *
     * @param htmlContent String with HTML content
     * @return Created object
     */
    public T fromHtml(String htmlContent) {
        Element pageRoot = Jsoup.parse(htmlContent);
        return loadFromNode(pageRoot, null);
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
     * Populates {@code T} instance from the provided {@code inputStream}.
     * <p>
     * Does not close the {@code InputStream}.
     *
     * @param inputStream InputStream with HTML content
     * @param charset Charset to use
     * @param baseUrl The URL where the HTML was retrieved from, to resolve relative links against.
     * @param instance instance to populate
     * @return Created object of type {@code T}
     * @throws IOException If I/O error occurs while reading the {@code InputStream}
     */
    public T fromInputStream(InputStream inputStream, Charset charset, URL baseUrl, T instance) throws IOException {
        String urlToUse = baseUrl != null ? baseUrl.toString() : null;
        String charsetToUse = charset != null ? charset.name() : null;
        Element root = Jsoup.parse(inputStream, charsetToUse, urlToUse);
        return loadFromNode(root, null);
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
        return loadFromNode(root, null);
    }

    private void addCachedHtmlField(FieldType field, Selector selector) {
        SelectorSpec spec = new SelectorSpec(selector, field);
        HtmlField<T> htmlField;
        if (spec.getConverter() != null) {
            htmlField = new HtmlFieldWithConverter<>(field, spec);
        } else if (isCollectionLike(field)) {
            htmlField = new HtmlCollectionLikeField<>(field, spec);
        } else if (Utils.isSimple(field.getType())) {
            htmlField = new HtmlSimpleField<>(field, spec);
        } else {
            htmlField = new HtmlClassField<>(field, spec);
        }
        htmlFieldCache.put(field.getName(), htmlField);
    }

    private boolean isCollectionLike(FieldType field) {
        return field.isArray() || field.isAssignableTo(Collection.class);
    }

    T loadFromNode(Element node) {
        return loadFromNode(node, null);
    }

    T loadFromNode(Element node, T instance) {
        if (instance == null) {
            instance = Utils.constructInstance(clazz);
        }
        for (HtmlField<T> htmlField : htmlFieldCache.values()) {
            htmlField.setValue(jspoon, node, instance);
        }
        return instance;
    }
}