package pl.droidsonroids.jspoon;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class HtmlCollectionLikeField<T> extends HtmlField<T> {
    HtmlCollectionLikeField(FieldType field, SelectorSpec spec) {
        super(field, spec);
    }

    @Override
    public void setValue(Jspoon jspoon, Element node, T newInstance) {
        Elements nodes = selectChildren(node);

        Class<?> componentClass = null;
        if (field.isArray()) {
            componentClass = field.getArrayContentType();
        } else {
            componentClass = field.getTypeArgumentCount() == 1 ?
                field.getTypeArgument(0) : Object.class;
            // Raw or <any> collections treated as Collection<String>
            componentClass = (componentClass == Object.class) ? String.class : componentClass;
        }

        Collection<?> collection = populateCollection(jspoon, nodes, componentClass);

        if (!field.isArray()) {
            setFieldOrThrow(field, newInstance, collection);
            return;
        }

        Object[] array = (Object[]) Array.newInstance(componentClass, collection.size());
        Iterator<?> it = collection.iterator();
        int index = 0;
        while (it.hasNext()) {
            array[index] = it.next();
            index++;
        }
        setFieldOrThrow(field, newInstance, array);
    }

    private <V> Collection<V> populateCollection(Jspoon jspoon, Elements nodes, Class<V> componentClazz) {
        @SuppressWarnings("unchecked")
        Collection<V> newInstanceList = (field.isArray() ?
               Utils.constructInstance(ArrayList.class) : createCollectionInstance(field));
        if (Utils.isSimple(componentClazz)) {
            for (Element node : nodes) {
                newInstanceList.add(instanceForNode(node, componentClazz));
            }
        } else {
            HtmlAdapter<V> htmlAdapter = jspoon.adapter(componentClazz);
            for (Element node : nodes) {
                newInstanceList.add(htmlAdapter.loadFromNode(node));
            }
        }
        return newInstanceList;
    }

    private <V> Collection<V> createCollectionInstance(FieldType field) {
        if (field.isConcrete()) {
            @SuppressWarnings("unchecked")
            Collection<V> collection = (Collection<V>) Utils.constructInstance(field.getType());
            return collection;
        }
        if (field.isAssignableTo(Set.class)) {
            return new LinkedHashSet<>();
        }
        return new ArrayList<>();
    }
}