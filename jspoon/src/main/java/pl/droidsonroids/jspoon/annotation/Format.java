package pl.droidsonroids.jspoon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Format {

    /**
     * @return To specify the format string, can be used where field's {@link java.text.Format}
     * is relevant, for example Number, Date and similar.
     */
    String value() default "";

    /**
     * @return To specify Locale string, can be used where field's {@link java.text.Format}
     * is relevant, for example Number, Date and similar.
     */
    String languageTag() default "";
}