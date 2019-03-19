package org.ccci.gto.android.common.jsonapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonApiAttribute {
    /**
     * Specify the name of this attribute when serialized to/deserialized from jsonapi.
     */
    String name() default "";

    /**
     * Alias for {@link JsonApiAttribute#name()}.
     */
    String value() default "";
}
