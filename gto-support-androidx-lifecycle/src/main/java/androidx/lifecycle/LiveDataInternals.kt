package androidx.lifecycle

import androidx.lifecycle.LiveData.START_VERSION

internal val LiveData<*>.isInitialized get() = version > START_VERSION
