package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.MutableLiveData

fun MutableLiveData<Boolean>.toggleValue() {
    value = value != true
}
