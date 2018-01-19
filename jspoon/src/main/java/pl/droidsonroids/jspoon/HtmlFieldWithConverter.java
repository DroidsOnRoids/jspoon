package pl.droidsonroids.jspoon;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.jsoup.nodes.Element;

import pl.droidsonroids.jspoon.annotation.Selector;
import pl.droidsonroids.jspoon.exception.ObjectCreationException;

class HtmlFieldWithConverter<T> extends HtmlField<T> {

    private final ElementConverter converter;
    private final Selector selector;

    HtmlFieldWithConverter(Field field, Selector selector) {
        super(field, selector);
        this.selector = selector;

        Class<? extends ElementConverter> converterClass = selector.converter();
        if (converterClass.equals(ElementConverter.class))
            throw new IllegalArgumentException("Expecting a concrete type of " + ElementConverter.class);

        try {
            Constructor<? extends ElementConverter> constructor = converterClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            converter = constructor.newInstance();
        } catch (Exception e) {
            throw new ObjectCreationException("Converter '" + converterClass.getSimpleName() +"' must "
                + "contain a no-arg constructor");
        }
    }

    @Override
    public void setValue(Jspoon jspoon, Element node, T newInstance) {
        Object converted = this.converter.convert(node, selector);
        setFieldOrThrow(field, newInstance, converted);
    }
}
