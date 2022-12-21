package org.ccci.gto.android.common.api

import android.content.SharedPreferences

class UserIdSession private constructor(
    prefs: SharedPreferences?,
    val userId: String?,
    sessionId: String?,
    baseAttrName: String,
) : Session(prefs = prefs, id = sessionId, baseAttrName = baseAttrName + userId?.let { "|$it" }.orEmpty()) {
    @JvmOverloads
    constructor(
        userId: String?,
        sessionId: String?,
        baseAttrName: String = PREF_SESSION_BASE_NAME,
    ) : this(prefs = null, userId = userId, sessionId = sessionId, baseAttrName = baseAttrName)

    @JvmOverloads
    constructor(
        prefs: SharedPreferences,
        userId: String?,
        baseAttrName: String = PREF_SESSION_BASE_NAME,
    ) : this(prefs = prefs, userId = userId, sessionId = null, baseAttrName = baseAttrName)

    override fun equals(other: Any?) = when {
        this === other -> true
        !(other is UserIdSession && javaClass == other.javaClass) -> false
        else -> userId == other.userId && super.equals(other)
    }

    override fun hashCode() = super.hashCode() * 31 + userId.hashCode()
}
