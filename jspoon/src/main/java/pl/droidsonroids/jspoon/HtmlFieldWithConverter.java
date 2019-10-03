package pl.droidsonroids.jspoon;

import org.jsoup.nodes.Element;

import pl.droidsonroids.jspoon.annotation.Selector;

class HtmlFieldWithConverter<T> extends HtmlField<T> {

    private final ElementConverter<?> converter;

    HtmlFieldWithConverter(FieldType field, SelectorSpec spec) {
        super(field, spec);

        Class<? extends ElementConverter<?>> converterClass = spec.getConverter();
        converter = Utils.constructInstance(converterClass);
    }

    @Override
    public void setValue(Jspoon jspoon, Element node, T newInstance) {
        Element selectedNode = selectChild(node);
        if (selectedNode != null) {
            Selector selectorAnnotation = spec.getSelectorAnnotation();
            Object converted = converter.convert(selectedNode, selectorAnnotation);
            setFieldOrThrow(field, newInstance, converted);
        }
    }
}
