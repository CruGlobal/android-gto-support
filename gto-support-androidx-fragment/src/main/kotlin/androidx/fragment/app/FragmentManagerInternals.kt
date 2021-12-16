package androidx.fragment.app

import org.ccci.gto.android.common.util.getDeclaredFieldOrNull

private val pendingActionsField by lazy { getDeclaredFieldOrNull<FragmentManager>("mPendingActions") }

internal val FragmentManager.pendingActions
    get() = pendingActionsField?.get(this) as? ArrayList<FragmentManager.OpGenerator>
