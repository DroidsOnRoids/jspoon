package pl.droidsonroids.jspoon;

import org.jsoup.nodes.Element;

import pl.droidsonroids.jspoon.annotation.Selector;

class HtmlFieldWithConverter<T> extends HtmlField<T> {

    private final ElementConverter<?> converter;
    private final Selector selector;

    HtmlFieldWithConverter(FieldType field, SelectorSpec spec) {
        super(field, spec);
        this.selector = spec.getSelectorAnnotation();

        Class<? extends ElementConverter<?>> converterClass = spec.getConverter();
        converter = Utils.constructInstance(converterClass);
    }

    @Override
    public void setValue(Jspoon jspoon, Element node, T newInstance) {
        Object converted = this.converter.convert(node, selector);
        setFieldOrThrow(field, newInstance, converted);
    }
}
