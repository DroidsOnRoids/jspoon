package pl.droidsonroids.jspoon;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Coordinates binding between HTML values and Java objects.
 */
public class Jspoon {

    private Map<Class<?>, HtmlAdapter<?>> adapterCache;

    /**
     * Creates a new Jspoon instance.
     *
     * @return a new Jspoon instance
     */
    public static Jspoon create() {
        return new Jspoon();
    }

    private Jspoon() {
        this.adapterCache = new ConcurrentHashMap<>();
    }

    /**
     * Returns a HTML adapter for {@code clazz}, creating it if necessary.
     *
     * @param clazz Class for creating objects
     * @param <T> Class for creating objects
     * @return {@link HtmlAdapter} instance
     */

    @SuppressWarnings("unchecked")
    public <T> HtmlAdapter<T> adapter(Class<T> clazz) {
        if (!adapterCache.containsKey(clazz)) {
            adapterCache.put(clazz, new HtmlAdapter<>(this, clazz));
        }
        return (HtmlAdapter<T>) adapterCache.get(clazz);
    }
}
