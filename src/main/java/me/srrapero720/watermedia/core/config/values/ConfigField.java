package me.srrapero720.watermedia.core.config.values;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigField {
    String validate() default "";
    RangeOf range() default @RangeOf;
}
