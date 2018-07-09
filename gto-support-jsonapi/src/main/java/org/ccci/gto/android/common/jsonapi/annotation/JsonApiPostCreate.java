package org.ccci.gto.android.common.jsonapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates a method to call once an object has been deserialized.
 * This will only be called on full objects and not on placeholder objects.
 * Execution order is undefined when multiple objects in an object graph have a {@link JsonApiPostCreate} annotation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonApiPostCreate {
}
