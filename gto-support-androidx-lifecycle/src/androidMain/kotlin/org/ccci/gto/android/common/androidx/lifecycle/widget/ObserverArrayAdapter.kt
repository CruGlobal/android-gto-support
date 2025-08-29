package org.ccci.gto.android.common.androidx.lifecycle.widget

import android.content.Context
import android.widget.ArrayAdapter
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.lifecycle.Observer

open class ObserverArrayAdapter<T>(context: Context, @LayoutRes layout: Int, @IdRes textViewResourceId: Int = 0) :
    ArrayAdapter<T>(context, layout, textViewResourceId), Observer<Collection<T>> {
    override fun onChanged(value: Collection<T>) {
        clear()
        addAll(value)
    }
}
