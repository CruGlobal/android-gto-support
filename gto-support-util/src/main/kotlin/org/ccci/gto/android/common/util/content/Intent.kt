@file:JvmMultifileClass
@file:JvmName("IntentKt")

package org.ccci.gto.android.common.util.content

import android.content.Intent
import org.ccci.gto.android.common.util.os.equalsBundle

// region equalsIntent()
@JvmName("intentEquals")
infix fun Intent?.equalsIntent(other: Intent?) = when {
    this === other -> true
    this == null -> false
    other == null -> false
    component != other.component -> false
    data != other.data -> false
    action != other.action -> false
    !(extras equalsBundle other.extras) -> false
    else -> true
}
// endregion equalsIntent()
