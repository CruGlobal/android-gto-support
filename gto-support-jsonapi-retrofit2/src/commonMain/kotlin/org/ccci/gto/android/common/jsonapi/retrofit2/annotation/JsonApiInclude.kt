package org.ccci.gto.android.common.jsonapi.retrofit2.annotation

/**
 * This annotation is used with request body serialization to include additional related objects in the generated JSON.
 * This is not part of the JsonApi spec, but is an extension used within some of our projects.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonApiInclude(vararg val value: String = [], val all: Boolean = false)
