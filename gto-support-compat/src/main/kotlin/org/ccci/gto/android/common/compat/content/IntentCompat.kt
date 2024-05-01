@file:JvmName("IntentCompat")

package org.ccci.gto.android.common.compat.content

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import java.io.Serializable

@JvmName("getParcelableExtra")
fun <T> Intent.getParcelableExtraCompat(name: String?, clazz: Class<T>) = COMPAT.getParcelableExtra(this, name, clazz)

@JvmName("getSerializableExtra")
fun <T : Serializable> Intent.getSerializableExtraCompat(name: String?, clazz: Class<T>) =
    COMPAT.getSerializableExtra(this, name, clazz)

private val COMPAT = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> TiramisuIntentCompatMethods()
    else -> BaseIntentCompatMethods()
}

private sealed interface IntentCompatMethods {
    fun <T> getParcelableExtra(intent: Intent, name: String?, clazz: Class<T>): T?
    fun <T : Serializable> getSerializableExtra(intent: Intent, name: String?, clazz: Class<T>): T?
}

private open class BaseIntentCompatMethods : IntentCompatMethods {
    override fun <T> getParcelableExtra(intent: Intent, name: String?, clazz: Class<T>): T? {
        @Suppress("DEPRECATION")
        val raw: Parcelable? = intent.getParcelableExtra(name)
        return if (clazz.isInstance(raw)) clazz.cast(raw) else null
    }

    override fun <T : Serializable> getSerializableExtra(intent: Intent, name: String?, clazz: Class<T>): T? {
        @Suppress("DEPRECATION")
        val raw = intent.getSerializableExtra(name)
        return if (clazz.isInstance(raw)) clazz.cast(raw) else null
    }
}

@TargetApi(Build.VERSION_CODES.TIRAMISU)
private class TiramisuIntentCompatMethods : BaseIntentCompatMethods() {
    override fun <T> getParcelableExtra(intent: Intent, name: String?, clazz: Class<T>) =
        intent.getParcelableExtra(name, clazz)

    override fun <T : Serializable> getSerializableExtra(intent: Intent, name: String?, clazz: Class<T>) =
        intent.getSerializableExtra(name, clazz)
}
