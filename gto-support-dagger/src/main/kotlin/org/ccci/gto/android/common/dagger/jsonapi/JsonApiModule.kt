package org.ccci.gto.android.common.dagger.jsonapi

import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import dagger.multibindings.Multibinds
import kotlin.reflect.KClass
import org.ccci.gto.android.common.jsonapi.converter.TypeConverter

@Module
abstract class JsonApiModule {
    @Multibinds
    abstract fun jsonApiConverters(): Set<TypeConverter<*>>

    @Multibinds
    @JsonApiModel
    abstract fun jsonApiModels(): Set<KClass<*>>

    companion object {
        @Provides
        @ElementsIntoSet
        @JsonApiModel
        fun convertJsonApiModelClasses(@JsonApiModel classes: Set<KClass<*>>) = classes.map { it.java }.toSet()
    }
}
