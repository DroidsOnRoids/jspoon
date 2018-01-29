package pl.droidsonroids.jspoon;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.jsoup.nodes.Element;

class Utils {
    static boolean isSimple(Class clazz) {
        return clazz.equals(String.class) ||
                clazz.equals(Integer.class) || clazz.equals(int.class) ||
                clazz.equals(Long.class) || clazz.equals(long.class) ||
                clazz.equals(Float.class) || clazz.equals(float.class) ||
                clazz.equals(Double.class) || clazz.equals(double.class) ||
                clazz.equals(Boolean.class) || clazz.equals(boolean.class) ||
                clazz.equals(BigDecimal.class) ||
                clazz.equals(Element.class) ||
                clazz.equals(List.class) ||
                clazz.equals(Date.class) ||
                clazz.equals(Element.class);
    }

    static Object getDefaultValueForType(Class clazz) {
        if (!clazz.isPrimitive())
            return null;
        if (clazz.equals(boolean.class))
            return false;
        if (clazz.equals(int.class)
            || clazz.equals(byte.class)
            || clazz.equals(short.class))
            return 0;
        if (clazz.equals(long.class))
            return 0L;
        if (clazz.equals(float.class))
            return 0F;
        if (clazz.equals(double.class))
            return 0D;
        if (clazz.equals(char.class))
            return '\u0000';

        throw new AssertionError("Missed primitive type: " + clazz);
    }
}
