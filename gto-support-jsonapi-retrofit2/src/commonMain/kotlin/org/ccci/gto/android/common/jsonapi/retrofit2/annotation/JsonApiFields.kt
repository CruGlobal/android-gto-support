package org.ccci.gto.android.common.jsonapi.retrofit2.annotation

import androidx.annotation.RestrictTo

/**
 * This annotation is used with request body serialization to limit fields that are serialized in the generated JSON.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@JvmRepeatable(JsonApiFields.Container::class)
annotation class JsonApiFields(
    val type: String,
    vararg val value: String = []
) {
    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.RUNTIME)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    annotation class Container(val value: Array<JsonApiFields>)
}
