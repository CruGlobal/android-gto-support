package org.ccci.gto.android.common.androidx.work

import androidx.work.Data
import java.util.Locale

fun Data.Builder.putLocale(key: String, value: Locale?) = putString(key, value?.toLanguageTag())
fun Data.getLocale(key: String) = getString(key)?.let { Locale.forLanguageTag(it) }
