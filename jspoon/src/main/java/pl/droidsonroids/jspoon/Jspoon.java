package pl.droidsonroids.jspoon;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Coordinates binding between HTML values and Java objects.
 */
public class Jspoon {
    private Map<String, HtmlAdapter<?>> adapterCache;

    /** Returns a new Jspoon instance. */
    public static Jspoon create() {
        return new Jspoon();
    }

    private Jspoon() {
        this.adapterCache = new LinkedHashMap<>();
    }

    /** Returns a HTML adapter for {@code clazz}, creating it if necessary. */
    @SuppressWarnings("unchecked")
    public <T> HtmlAdapter<T> adapter(Class<T> clazz) {
        String key = clazz.getName();
        if (adapterCache.containsKey(key)) {
            return (HtmlAdapter<T>) adapterCache.get(key);
        } else {
            HtmlAdapter<T> htmlAdapter = new HtmlAdapter<>(this, clazz);
            adapterCache.put(key, htmlAdapter);
            return htmlAdapter;
        }
    }
}
