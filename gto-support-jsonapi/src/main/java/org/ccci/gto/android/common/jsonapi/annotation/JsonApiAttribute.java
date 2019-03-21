package org.ccci.gto.android.common.jsonapi.annotation;

import org.ccci.gto.android.common.jsonapi.JsonApiConverter;

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

    /**
     * Should this attribute be serialized by the {@link JsonApiConverter}
     */
    boolean serialize() default true;

    /**
     * Should this attribute be deserialized by the {@link JsonApiConverter}
     */
    boolean deserialize() default true;
}
