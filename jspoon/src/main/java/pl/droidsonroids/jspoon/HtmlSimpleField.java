package pl.droidsonroids.jspoon;

import java.lang.reflect.Field;
import org.jsoup.nodes.Element;
import pl.droidsonroids.jspoon.annotation.Selector;

class HtmlSimpleField<T> extends HtmlField<T> {
    HtmlSimpleField(Field field, Selector selector) {
        super(field, selector);
    }

    @Override
    public void setValue(Jspoon jspoon, Element node, T newInstance) {
        Element selectedNode = selectChild(node);
        setFieldOrThrow(field, newInstance, instanceForNode(selectedNode, field.getType()));
    }
}