package org.ccci.gto.android.common.androidx.fragment.app

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit

fun DialogFragment.showAllowingStateLoss(manager: FragmentManager, tag: String?) {
    manager.commit(true) { add(this@showAllowingStateLoss, tag) }
}
