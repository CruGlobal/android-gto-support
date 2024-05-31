package org.ccci.gto.android.common.dagger.jsonapi

import dagger.Module
import dagger.multibindings.Multibinds
import org.ccci.gto.android.common.jsonapi.converter.TypeConverter

@Module
abstract class JsonApiModule {
    @Multibinds
    abstract fun jsonApiConverters(): Set<TypeConverter<*>>

    @Multibinds
    @JsonApiClass
    abstract fun jsonApiClasses(): Set<Class<*>>
}
