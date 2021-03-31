package org.ccci.gto.android.common.androidx.drawerlayout.widget

import androidx.drawerlayout.widget.DrawerLayout

fun DrawerLayout.toggleDrawer(gravity: Int, animate: Boolean = true) {
    when {
        isDrawerOpen(gravity) -> closeDrawer(gravity, animate)
        else -> openDrawer(gravity, animate)
    }
}
