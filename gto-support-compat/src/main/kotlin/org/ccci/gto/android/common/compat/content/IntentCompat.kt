@file:JvmName("IntentCompat")
package org.ccci.gto.android.common.compat.content

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import java.io.Serializable

@JvmName("getSerializableExtra")
fun <T : Serializable> Intent.getSerializableExtraCompat(name: String?, clazz: Class<T>) =
    COMPAT.getSerializableExtra(this, name, clazz)

private val COMPAT = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> TiramisuIntentCompatMethods()
    else -> BaseIntentCompatMethods()
}

private sealed interface IntentCompatMethods {
    fun <T : Serializable> getSerializableExtra(intent: Intent, name: String?, clazz: Class<T>): T?
}

private open class BaseIntentCompatMethods : IntentCompatMethods {
    override fun <T : Serializable> getSerializableExtra(intent: Intent, name: String?, clazz: Class<T>): T? {
        @Suppress("DEPRECATION")
        val raw = intent.getSerializableExtra(name)
        return if (clazz.isInstance(raw)) clazz.cast(raw) else null
    }
}

@TargetApi(Build.VERSION_CODES.TIRAMISU)
private class TiramisuIntentCompatMethods : BaseIntentCompatMethods() {
    override fun <T : Serializable> getSerializableExtra(intent: Intent, name: String?, clazz: Class<T>) =
        intent.getSerializableExtra(name, clazz)
}
