package org.ccci.gto.android.common.androidx.drawerlayout.widget

import androidx.drawerlayout.widget.DrawerLayout

@Deprecated("Since v4.5.1, use Compose for building UIs")
fun DrawerLayout.toggleDrawer(gravity: Int, animate: Boolean = true) {
    when {
        isDrawerOpen(gravity) -> closeDrawer(gravity, animate)
        else -> openDrawer(gravity, animate)
    }
}
