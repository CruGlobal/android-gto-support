package org.ccci.gto.android.common.api

import android.content.SharedPreferences
import java.util.Locale

class TheKeySession : Session {
    private val guid: String?

    @JvmOverloads
    constructor(
        id: String?,
        guid: String?,
        baseAttrName: String = PREF_SESSION_BASE_NAME
    ) : super(id, guid.sanitizeGuid(".") + baseAttrName) {
        this.guid = guid?.sanitizeGuid()
    }

    @JvmOverloads
    constructor(
        prefs: SharedPreferences,
        guid: String?,
        baseAttrName: String = PREF_SESSION_BASE_NAME
    ) : super(prefs, guid.sanitizeGuid(".") + baseAttrName) {
        this.guid = guid?.sanitizeGuid()
    }

    override fun isValid() = super.isValid() && guid != null

    override fun equals(other: Any?) = when {
        this === other -> true
        !(other is TheKeySession && javaClass == other.javaClass) -> false
        else -> super.equals(other) && guid == other.guid
    }

    override fun hashCode() = super.hashCode() * 31 + (guid?.hashCode() ?: 0)
}

private fun String?.sanitizeGuid(suffix: String = "") = if (this != null) "${toUpperCase(Locale.US)}$suffix" else ""
