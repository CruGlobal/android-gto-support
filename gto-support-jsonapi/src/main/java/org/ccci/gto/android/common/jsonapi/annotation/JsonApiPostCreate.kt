package org.ccci.gto.android.common.jsonapi.annotation

/**
 * This annotation indicates a method to call once an object has been deserialized.
 * This will only be called on full objects and not on placeholder objects.
 * Execution order is undefined when multiple objects in an object graph have a [JsonApiPostCreate] annotation.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonApiPostCreate
