package org.ccci.gto.android.common.jsonapi.internal.util

import androidx.annotation.RestrictTo
import java.lang.reflect.Array
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object ReflectionUtils {
    // logic copied from retrofit2.Utils.getRawType and converted to Kotlin
    @JvmStatic
    fun getRawType(type: Type?): Class<*> = when (type) {
        null -> error("type == null")
        // Type is a normal class.
        is Class<*> -> type
        is ParameterizedType -> {
            // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
            // suspects some pathological case related to nested classes exists.
            val rawType = type.rawType
            require(rawType is Class<*>) { "Unsupported ParameterizedType: $type" }
            rawType
        }

        is GenericArrayType -> Array.newInstance(getRawType(type.genericComponentType), 0).javaClass
        // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
        // type that's more general than necessary is okay.
        is TypeVariable<*> -> Any::class.java
        is WildcardType -> getRawType(type.upperBounds[0])
        else -> throw IllegalArgumentException(
            "Expected a Class, ParameterizedType, or GenericArrayType, but <$type> is of type ${type.javaClass.name}"
        )
    }
}
