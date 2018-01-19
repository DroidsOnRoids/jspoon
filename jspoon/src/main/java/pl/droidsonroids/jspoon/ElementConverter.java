package pl.droidsonroids.jspoon;

import org.jsoup.nodes.Element;

import pl.droidsonroids.jspoon.annotation.Selector;

/**
 * Interface for implementing a custom converter for an {@link Element element}.
 *
 * @param <T> Expected type
 * @see Selector#converter()
 */
public interface ElementConverter<T> {

    /**
     * Creates an object of type {@code T} based from the provided {@code node}.
     *
     * @param node The matched {@code element}
     * @param selector The annotation where this converter is registered
     * @return the value to be assigned to the field
     */
    T convert(Element node, Selector selector);
}
