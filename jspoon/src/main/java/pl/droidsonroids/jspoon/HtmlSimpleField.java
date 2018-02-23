package pl.droidsonroids.jspoon;

import org.jsoup.nodes.Element;

class HtmlSimpleField<T> extends HtmlField<T> {
    HtmlSimpleField(FieldType field, SelectorSpec spec) {
        super(field, spec);
    }

    @Override
    public void setValue(Jspoon jspoon, Element node, T newInstance) {
        Element selectedNode = selectChild(node);
        setFieldOrThrow(field, newInstance, instanceForNode(selectedNode, field.getType()));
    }
}