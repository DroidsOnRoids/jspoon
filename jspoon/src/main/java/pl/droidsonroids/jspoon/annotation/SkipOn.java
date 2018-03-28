package pl.droidsonroids.jspoon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * In case an exception is thrown during element conversion to field's value, if that exception is
 * of type or sub-type of any {@link Throwable} specified in that field's {@code SkipOn} annotation
 * such exception will be ignored and that field will skip mapping.
 *
 * For example {@code @SkipOn(NumberFormatException.class)}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface SkipOn {

    /**
     * @return classes of a Throwable instance that should be ignored if thrown while converting this field
     */
    Class<? extends Throwable>[] value() default {};
}