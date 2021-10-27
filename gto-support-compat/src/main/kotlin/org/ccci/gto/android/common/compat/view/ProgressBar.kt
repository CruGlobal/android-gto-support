@file:JvmName("ProgressBarCompat")

package org.ccci.gto.android.common.compat.view

import android.os.Build
import android.widget.ProgressBar

@JvmName("setProgress")
fun ProgressBar.setProgressCompat(progress: Int, animate: Boolean) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> setProgress(progress, animate)
    else -> setProgress(progress)
}
