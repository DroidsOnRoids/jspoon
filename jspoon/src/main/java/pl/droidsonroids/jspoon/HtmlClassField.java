package pl.droidsonroids.jspoon;

import java.lang.reflect.Field;
import org.jsoup.nodes.Element;
import pl.droidsonroids.jspoon.annotation.Selector;

class HtmlClassField<T> extends HtmlField<T> {
    HtmlClassField(Field field, Selector selector) {
        super(field, selector);
    }

    @Override
    public void setValue(Jspoon jspoon, Element node, T newInstance) {
        HtmlAdapter htmlAdapter = jspoon.adapter(field.getType());
        Element selectedNode = selectChild(node);
        if (selectedNode != null) {
            setFieldOrThrow(field, newInstance, htmlAdapter.loadFromNode(selectedNode));
        }
    }
}