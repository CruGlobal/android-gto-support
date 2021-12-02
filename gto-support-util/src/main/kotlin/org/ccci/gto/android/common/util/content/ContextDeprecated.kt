package org.ccci.gto.android.common.util.content

import android.content.Context

@Deprecated("Since v3.10.1, use extension property instead.", ReplaceWith("isApplicationDebuggable"))
fun Context.isApplicationDebuggable() = isApplicationDebuggable
