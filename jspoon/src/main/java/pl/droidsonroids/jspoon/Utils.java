package pl.droidsonroids.jspoon;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;

import pl.droidsonroids.jspoon.exception.ConstructorNotFoundException;
import pl.droidsonroids.jspoon.exception.ObjectCreationException;

public class Utils {

    /** Map of class representing primitives and their object counterparts. */
    private final static Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = new HashMap<Class<?>, Class<?>>();

    static {
        PRIMITIVE_WRAPPERS.put(boolean.class, Boolean.class);
        PRIMITIVE_WRAPPERS.put(byte.class, Byte.class);
        PRIMITIVE_WRAPPERS.put(short.class, Short.class);
        PRIMITIVE_WRAPPERS.put(char.class, Character.class);
        PRIMITIVE_WRAPPERS.put(int.class, Integer.class);
        PRIMITIVE_WRAPPERS.put(long.class, Long.class);
        PRIMITIVE_WRAPPERS.put(float.class, Float.class);
        PRIMITIVE_WRAPPERS.put(double.class, Double.class);
    }

    /**
     * If the given class is a primitive will return its Object class representation, otherwise
     * returns the same given class.
     * @param clazz
     * @return object class from a possible primitive
     */
    public static <T> Class<T> wrapToObject(Class<T> clazz){
        if (!clazz.isPrimitive()) {
            return clazz;
        }
        @SuppressWarnings("unchecked")
        Class<T> wrapped = (Class<T>) PRIMITIVE_WRAPPERS.get(clazz);
        return (wrapped != null) ? wrapped : clazz;
    }


    public static <T> T constructInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new ConstructorNotFoundException(clazz.getSimpleName());
        } catch (Exception e) {
            throw new ObjectCreationException(clazz.getSimpleName());
        }
    }

    /**
     * Returns a list of all fields, including inherited, starting from the upper most superclass.
     * @param target the target subclass
     * @return all declared fields
     */
    public static List<Field> getAllDeclaredFields(Class<?> target) {
        List<Field> declaredFields = new ArrayList<>();
        if (target.getSuperclass() == null || target == Object.class || target.isInterface()){
            return declaredFields;
        }
        declaredFields = getAllDeclaredFields(target.getSuperclass());
        for (Field field : target.getDeclaredFields()){
            declaredFields.add(field);
        }
        return declaredFields;
    }

    static boolean isSimple(Class<?> clazz) {
        return clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(int.class)
                || clazz.equals(Long.class) || clazz.equals(long.class) || clazz.equals(Float.class)
                || clazz.equals(float.class) || clazz.equals(Double.class)
                || clazz.equals(double.class) || clazz.equals(Boolean.class)
                || clazz.equals(boolean.class) || clazz.equals(BigDecimal.class)
                || clazz.equals(Element.class) || clazz.equals(List.class)
                || clazz.equals(Date.class) || clazz.equals(Element.class);
    }
}