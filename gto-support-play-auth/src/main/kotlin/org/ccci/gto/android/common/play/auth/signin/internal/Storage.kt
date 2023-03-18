package org.ccci.gto.android.common.play.auth.signin.internal

import android.content.Context
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import org.ccci.gto.android.common.kotlin.coroutines.getChangeFlow

private const val PREFS = "com.google.android.gms.signin"

internal fun Context.getStorageChangeFlow() = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    .getChangeFlow(null)
    .map { }
    .conflate()
