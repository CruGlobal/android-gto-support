package org.ccci.gto.android.common.dagger

import dagger.Lazy
import javax.inject.Provider
import kotlin.reflect.KProperty

operator fun <T> Lazy<T>.getValue(thisObj: Any?, property: KProperty<*>): T = get()
operator fun <T> Provider<T>.getValue(thisObj: Any?, property: KProperty<*>): T = get()
