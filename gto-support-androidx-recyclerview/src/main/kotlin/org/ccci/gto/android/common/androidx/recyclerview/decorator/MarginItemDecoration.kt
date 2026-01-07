package org.ccci.gto.android.common.androidx.recyclerview.decorator

import android.graphics.Rect
import android.view.View
import android.view.View.LAYOUT_DIRECTION_RTL
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    margins: Int = 0,
    horizontalMargins: Int = margins,
    verticalMargins: Int = margins,
    @Px private val leftMargin: Int = horizontalMargins,
    @Px private val topMargin: Int = verticalMargins,
    @Px private val rightMargin: Int = horizontalMargins,
    @Px private val bottomMargin: Int = verticalMargins,
    @Px private val startMargin: Int? = null,
    @Px private val endMargin: Int? = null,
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = topMargin
        outRect.bottom = bottomMargin

        when (view.layoutDirection) {
            LAYOUT_DIRECTION_RTL -> {
                outRect.left = endMargin ?: leftMargin
                outRect.right = startMargin ?: rightMargin
            }

            else -> {
                outRect.left = startMargin ?: leftMargin
                outRect.right = endMargin ?: rightMargin
            }
        }
    }
}
