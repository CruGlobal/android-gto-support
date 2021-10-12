package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.LiveData

class ImmutableLiveData<T>(value: T) : LiveData<T>(value)
