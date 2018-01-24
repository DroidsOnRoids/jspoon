package pl.droidsonroids.jspoon;

import java.lang.reflect.Field;

import org.jsoup.nodes.Element;

import pl.droidsonroids.jspoon.annotation.Selector;

class HtmlFieldWithConverter<T> extends HtmlField<T> {

    private final ElementConverter<?> converter;
    private final Selector selector;

    HtmlFieldWithConverter(Field field, Selector selector) {
        super(field, selector);
        this.selector = selector;

        @SuppressWarnings("rawtypes")
        Class<? extends ElementConverter> converterClass = selector.converter();
        if (converterClass.equals(ElementConverter.class)){
            throw new IllegalArgumentException("Expecting a concrete type of " + ElementConverter.class);
        }

        converter = Utils.constructInstance(converterClass);
    }

    @Override
    public void setValue(Jspoon jspoon, Element node, T newInstance) {
        Object converted = this.converter.convert(node, selector);
        setFieldOrThrow(field, newInstance, converted);
    }
}
