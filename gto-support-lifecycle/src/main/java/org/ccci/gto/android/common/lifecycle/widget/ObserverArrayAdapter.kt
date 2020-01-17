package org.ccci.gto.android.common.lifecycle.widget

import android.content.Context
import android.widget.ArrayAdapter
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.lifecycle.Observer

open class ObserverArrayAdapter<T>(context: Context, @LayoutRes layout: Int, @IdRes textViewResourceId: Int = 0) :
    ArrayAdapter<T>(context, layout, textViewResourceId), Observer<Collection<T>> {
    override fun onChanged(t: Collection<T>?) {
        clear()
        if (t != null) addAll(t)
    }
}
