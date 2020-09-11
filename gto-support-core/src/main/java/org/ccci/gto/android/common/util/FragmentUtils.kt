package org.ccci.gto.android.common.util

import androidx.fragment.app.Fragment
import org.ccci.gto.android.common.androidx.fragment.app.findAncestorFragment
import org.ccci.gto.android.common.androidx.fragment.app.findListener

@Deprecated(
    "Since v3.6.2, use findAncestorFragment() from gto-support-androidx-fragment module instead",
    ReplaceWith("findAncestorFragment<T>()", "org.ccci.gto.android.common.androidx.fragment.app.findAncestorFragment")
)
inline fun <reified T> Fragment.findAncestorFragment() = findAncestorFragment<T>()

@Deprecated(
    "Since v3.6.2, use findListener() from gto-support-androidx-fragment module instead",
    ReplaceWith("findListener()", "org.ccci.gto.android.common.androidx.fragment.app.findListener")
)
inline fun <reified T> Fragment.findListener() = findListener<T>()
