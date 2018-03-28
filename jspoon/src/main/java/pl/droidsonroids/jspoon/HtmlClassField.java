package pl.droidsonroids.jspoon;

import org.jsoup.nodes.Element;

class HtmlClassField<T> extends HtmlField<T> {
    HtmlClassField(FieldType field, SelectorSpec selector) {
        super(field, selector);
    }

    @Override
    public void setValue(Jspoon jspoon, Element node, T newInstance) {
        HtmlAdapter<?> htmlAdapter = jspoon.adapter(field.getType());
        Element selectedNode = selectChild(node);
        if (selectedNode != null) {
            setFieldOrThrow(field, newInstance, htmlAdapter.loadFromNode(selectedNode));
        }
    }
}