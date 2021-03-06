package org.ccci.gto.android.common.androidx.fragment.app

import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.findAncestorFragment(): T? {
    var candidate = parentFragment
    while (candidate != null) {
        if (candidate is T) return candidate
        candidate = candidate.parentFragment
    }
    return null
}

inline fun <reified T> Fragment.findListener(): T? = targetFragment as? T ?: findAncestorFragment() ?: activity as? T
