package org.ccci.gto.android.common.jsonapi.annotation

/**
 * This annotation indicates this field will reflect whether the deserialized object is a placeholder object only.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonApiPlaceholder
