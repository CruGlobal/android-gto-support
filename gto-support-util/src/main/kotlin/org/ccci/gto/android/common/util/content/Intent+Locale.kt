@file:JvmMultifileClass
@file:JvmName("IntentKt")

package org.ccci.gto.android.common.util.content

import android.content.Intent
import java.io.Serializable
import java.util.Locale

fun Intent.putExtra(key: String, value: Locale?, asString: Boolean = false) =
    if (asString) putExtra(key, value?.toLanguageTag()) else putExtra(key, value as? Serializable)

fun Intent.getLocaleExtra(key: String) =
    getSerializableExtra(key) as? Locale ?: getStringExtra(key)?.let { Locale.forLanguageTag(it) }
