package org.ccci.gto.android.common.dagger.jsonapi

import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import javax.inject.Singleton
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import org.ccci.gto.android.common.jsonapi.converter.LocaleTypeConverter
import org.ccci.gto.android.common.jsonapi.converter.TypeConverter
import org.junit.Test

class JsonApiModuleTest {
    // region No Additional Classes or Converters
    @Test
    fun `JsonApiModule - No Additional Classes or Converters`() {
        val component = DaggerJsonApiModuleTest_EmptyComponent.create()
        assertEquals(0, component.converters().size)
        assertEquals(0, component.classes().size)
    }

    @Singleton
    @Component(modules = [JsonApiModule::class])
    interface EmptyComponent {
        @JsonApiModel
        fun classes(): Set<Class<*>>
        fun converters(): Set<TypeConverter<*>>
    }
    // endregion No Additional Classes or Converters

    // region Additional Classes and Converters
    @Test
    fun `JsonApiModule - Additional Classes and Converters`() {
        val component = DaggerJsonApiModuleTest_AdditionalClassesAndConvertersComponent.create()
        assertEquals(setOf(LocaleTypeConverter), component.converters())
        assertEquals(setOf(String::class.java, Int::class.java), component.classes())
    }

    @Module
    object AdditionalClassesAndConvertersModule {
        @Provides
        @IntoSet
        fun localeConverter(): TypeConverter<*> = LocaleTypeConverter

        @Provides
        @IntoSet
        @JsonApiModel
        fun stringClass(): Class<*> = String::class.java

        @Provides
        @IntoSet
        @JsonApiModel
        fun intKClass(): KClass<*> = Int::class
    }

    @Singleton
    @Component(modules = [JsonApiModule::class, AdditionalClassesAndConvertersModule::class])
    interface AdditionalClassesAndConvertersComponent {
        @JsonApiModel
        fun classes(): Set<Class<*>>
        fun converters(): Set<TypeConverter<*>>
    }
    // endregion No Additional Classes or Converters
}
