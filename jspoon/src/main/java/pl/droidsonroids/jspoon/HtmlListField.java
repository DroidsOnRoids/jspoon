package pl.droidsonroids.jspoon;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.droidsonroids.jspoon.annotation.Selector;

class HtmlListField<T> extends HtmlField<T> {
    HtmlListField(Field field, Selector selector) {
        super(field, selector);
    }

    @Override
    public void setValue(Jspoon jspoon, Element node, T newInstance) {
        Elements nodes = selectChildren(node);

        Type genericType = field.getGenericType();
        Type type = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        Class<?> listClass = (Class<?>) type;

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