package org.ccci.gto.android.common.androidx.recyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

class SimpleLayoutAdapter(
    @LayoutRes private val layoutId: Int,
    repeat: Int = 1,
    private val initializeLayout: (View) -> Unit = {},
) : RecyclerView.Adapter<SimpleLayoutAdapter.ViewHolder>() {
    init {
        setHasStableIds(true)
    }

    var repeat = repeat
        set(value) {
            val old = field
            field = value
            when {
                value < old -> notifyItemRangeRemoved(value, old - value)
                value > old -> notifyItemRangeInserted(value, value - old)
            }
        }

    override fun getItemCount() = repeat
    override fun getItemId(position: Int) = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
            .also { initializeLayout(it) }
            .let { ViewHolder(it) }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = Unit

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
