package org.ccci.gto.android.common.lifecycle.databinding

import androidx.databinding.Observable
import kotlin.reflect.KProperty0
import org.ccci.gto.android.common.androidx.lifecycle.databinding.getPropertyLiveData

@Deprecated(
    "Since v3.4.0, use the extension method from gto-support-androidx-lifecycle instead", ReplaceWith(
        "getPropertyLiveData(property, *propertyIds)",
        "org.ccci.gto.android.common.androidx.lifecycle.databinding.getPropertyLiveData"
    )
)
fun <T> Observable.getPropertyLiveData(property: KProperty0<T>, vararg propertyIds: Int) =
    getPropertyLiveData(property, *propertyIds)
