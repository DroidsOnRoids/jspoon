package pl.droidsonroids.jspoon;

import java.util.Date;
import java.util.List;
import org.jsoup.nodes.Element;

class Utils {
    static boolean isSimple(Class clazz) {
        return clazz.equals(String.class) ||
                clazz.equals(Integer.class) || clazz.getSimpleName().equals("int") ||
                clazz.equals(Float.class) || clazz.getSimpleName().equals("float") ||
                clazz.equals(Boolean.class) || clazz.getSimpleName().equals("boolean") ||
                clazz.equals(Element.class) ||
                clazz.equals(List.class) ||
                clazz.equals(Date.class);
    }
}
