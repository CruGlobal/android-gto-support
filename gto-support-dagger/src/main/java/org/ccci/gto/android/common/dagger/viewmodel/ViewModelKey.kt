package org.ccci.gto.android.common.dagger.viewmodel

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

@Deprecated("Since v3.7.2, use Hilt ViewModel support instead")
@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class ViewModelKey(val value: KClass<out ViewModel>)
