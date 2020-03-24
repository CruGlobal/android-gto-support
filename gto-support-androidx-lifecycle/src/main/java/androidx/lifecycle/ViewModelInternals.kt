package androidx.lifecycle

internal fun <T : Any> ViewModel.setTagIfAbsent(key: String, value: T?): T? = setTagIfAbsent(key, value)
