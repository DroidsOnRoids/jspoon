package pl.droidsonroids.jspoon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jsoup.nodes.Element;
import pl.droidsonroids.jspoon.HtmlAdapter;

/**
 * Annotates a field to be mapped to a html element.
 *
 * A field annotated with this will receive the value corresponding to it's css
 * selector when the {@link HtmlAdapter#fromHtml(String)} is called.
 *
 * The field type can be any class or one of
 * the following types (or its primitive):
 * String
 * Float
 * Integer
 * Boolean
 * Date
 * {@link Element}
 * Or a List of any of these types.
 *
 * Class can be also annotated with @Selector. Then there is no need to
 * annotate every field where with object of this class.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Selector {
    String NO_VALUE = "NO_VALUE";

    /** Css query */
    String value();

    /** Attribute or property of selected field. "text" is default. Also "html"/"innerHtml" or "outerHtml" is supported. */
    String attr() default "";

    /** Regex for numbers and String, date format for Date. */
    String format() default NO_VALUE;

    /** Locale string, used for Date and Float */
    String locale() default NO_VALUE;

    /** Default String value if selected HTML element is empty */
    String defValue() default NO_VALUE;

    /** Index of found HTML element */
    int index() default 0;
}
