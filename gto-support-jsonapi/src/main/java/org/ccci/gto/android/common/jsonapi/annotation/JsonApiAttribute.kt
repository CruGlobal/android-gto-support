package org.ccci.gto.android.common.jsonapi.annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonApiAttribute(
    /**
     * Specify the name of this attribute when serialized to/deserialized from jsonapi.
     */
    val name: String = "",
    /**
     * Alias for [JsonApiAttribute.name].
     */
    val value: String = "",
    /**
     * Should this attribute be serialized by the [JsonApiConverter]
     */
    val serialize: Boolean = true,
    /**
     * Should this attribute be deserialized by the [JsonApiConverter]
     */
    val deserialize: Boolean = true,
)
