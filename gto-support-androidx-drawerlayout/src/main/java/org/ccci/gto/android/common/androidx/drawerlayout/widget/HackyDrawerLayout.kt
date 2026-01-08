package org.ccci.gto.android.common.androidx.drawerlayout.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.drawerlayout.R
import androidx.drawerlayout.widget.DrawerLayout
import org.ccci.gto.android.common.util.view.ViewUtils

@Deprecated("Since v4.5.1, use Compose for building UIs")
class HackyDrawerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.drawerLayoutStyle,
) : DrawerLayout(context, attrs, defStyleAttr) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent) = try {
        super.onTouchEvent(ev)
    } catch (e: RuntimeException) {
        ViewUtils.handleOnTouchEventException(e)
    }
}
