package org.ccci.gto.android.common.api

import android.content.SharedPreferences
import java.util.Locale

@Deprecated("Since v4.0.0, We should no longer be using The Key for authentication")
class TheKeySession private constructor(
    prefs: SharedPreferences?,
    id: String?,
    guid: String?,
    baseAttrName: String,
) : Session(prefs = prefs, id = id, baseAttrName = guid.sanitizeGuid(".") + baseAttrName) {
    private val guid = guid?.sanitizeGuid()

    @JvmOverloads
    constructor(
        id: String?,
        guid: String?,
        baseAttrName: String = PREF_SESSION_BASE_NAME,
    ) : this(prefs = null, id = id, guid = guid, baseAttrName = baseAttrName)

    @JvmOverloads
    constructor(
        prefs: SharedPreferences,
        guid: String?,
        baseAttrName: String = PREF_SESSION_BASE_NAME,
    ) : this(prefs = prefs, id = null, guid = guid, baseAttrName = baseAttrName)

    override val isValid get() = super.isValid && guid != null

    override fun equals(other: Any?) = when {
        this === other -> true
        !(other is TheKeySession && javaClass == other.javaClass) -> false
        else -> super.equals(other) && guid == other.guid
    }

    override fun hashCode() = super.hashCode() * 31 + (guid?.hashCode() ?: 0)
}

private fun String?.sanitizeGuid(suffix: String = "") = if (this != null) "${uppercase(Locale.US)}$suffix" else ""
