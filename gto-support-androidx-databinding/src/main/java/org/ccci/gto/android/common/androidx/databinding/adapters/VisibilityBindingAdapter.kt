package org.ccci.gto.android.common.androidx.databinding.adapters

import android.view.View
import androidx.databinding.BindingAdapter

private const val VISIBLE_IF = "visibleIf"
private const val INVISIBLE_IF = "invisibleIf"
private const val GONE_IF = "goneIf"

@BindingAdapter(VISIBLE_IF)
fun View.visibleIf(visible: Boolean) = visibility(visible = visible, gone = !visible)

@BindingAdapter(INVISIBLE_IF)
fun View.invisibleIf(invisible: Boolean) = visibility(invisible = invisible, visible = !invisible)

@BindingAdapter(GONE_IF)
fun View.goneIf(gone: Boolean) = visibility(gone = gone, visible = !gone)

@BindingAdapter(GONE_IF, INVISIBLE_IF)
fun View.visibilityGoneInvisible(gone: Boolean, invisible: Boolean) =
    visibility(gone = gone, invisible = invisible, visible = !gone && !invisible)

@BindingAdapter(VISIBLE_IF, INVISIBLE_IF)
fun View.visibilityVisibleInvisible(visible: Boolean, invisible: Boolean) =
    visibility(visible = visible, invisible = invisible)

@BindingAdapter(VISIBLE_IF, INVISIBLE_IF, GONE_IF)
fun View.visibility(visible: Boolean = true, invisible: Boolean = false, gone: Boolean = false) {
    visibility = when {
        gone -> View.GONE
        invisible -> View.INVISIBLE
        visible -> View.VISIBLE
        else -> View.GONE
    }
}
