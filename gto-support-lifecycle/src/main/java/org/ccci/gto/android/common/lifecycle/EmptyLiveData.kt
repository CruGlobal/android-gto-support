package org.ccci.gto.android.common.lifecycle

import androidx.lifecycle.LiveData
import org.ccci.gto.android.common.androidx.lifecycle.emptyLiveData
import org.ccci.gto.android.common.androidx.lifecycle.orEmpty

@Deprecated(
    "Since v3.4.0, use version in gto-support-androidx-lifecycle directly",
    ReplaceWith("emptyLiveData()", "org.ccci.gto.android.common.androidx.lifecycle.emptyLiveData")
)
fun <T> emptyLiveData(): LiveData<T?> = emptyLiveData()

@Deprecated(
    "Since v3.4.0, use version in gto-support-androidx-lifecycle directly",
    ReplaceWith("orEmpty()", "org.ccci.gto.android.common.androidx.lifecycle.orEmpty")
)
fun <T> LiveData<T>?.orEmpty() = orEmpty()
