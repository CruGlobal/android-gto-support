package org.ccci.gto.android.common.jsonapi.retrofit2.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used with request body serialization to include additional related objects in the generated JSON.
 * This is not part of the JsonApi spec, but is an extension used within some of our projects.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonApiInclude {
    String[] value() default {};

    boolean all() default false;
}
