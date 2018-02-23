package pl.droidsonroids.jspoon;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class HtmlListField<T> extends HtmlField<T> {
    HtmlListField(FieldType field, SelectorSpec spec) {
        super(field, spec);
    }

    @Override
    public void setValue(Jspoon jspoon, Element node, T newInstance) {
        Elements nodes = selectChildren(node);

        Class<?> listClass = field.getTypeArgumentCount() == 1 ?
                field.getTypeArgument(0) : Object.class;
        // Raw or <any> collections treated as Collection<String>
        listClass = (listClass == Object.class) ? String.class : listClass;

        setFieldOrThrow(field, newInstance, populateList(jspoon, nodes, listClass));
    }

    private <V> List<V> populateList(Jspoon jspoon, Elements nodes, Class<V> listClazz) {
        List<V> newInstanceList = new ArrayList<>();
        if (Utils.isSimple(listClazz)) {
            for (Element node : nodes) {
                newInstanceList.add(instanceForNode(node, listClazz));
            }
        } else {
            HtmlAdapter<V> htmlAdapter = jspoon.adapter(listClazz);
            for (Element node : nodes) {
                newInstanceList.add(htmlAdapter.loadFromNode(node));
            }
        }
        return newInstanceList;
    }
}