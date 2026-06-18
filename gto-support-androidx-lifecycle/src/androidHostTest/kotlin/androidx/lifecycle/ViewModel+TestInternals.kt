package androidx.lifecycle

import android.annotation.SuppressLint

@SuppressLint("RestrictedApi")
internal fun ViewModel.clear() {
    ViewModelStore().also {
        it.put("tmp", this)
        it.clear()
    }
}
