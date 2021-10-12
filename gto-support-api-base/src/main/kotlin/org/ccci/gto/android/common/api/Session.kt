package org.ccci.gto.android.common.api

import android.content.SharedPreferences

private const val PREF_ID = "id"

/**
 * Object representing an individual session for this API. Can be extended to track additional session data.
 */
open class Session protected constructor(
    prefs: SharedPreferences? = null,
    id: String? = null,
    private val baseAttrName: String = PREF_SESSION_BASE_NAME
) {
    companion object {
        const val PREF_SESSION_BASE_NAME = "session"
    }

    @JvmField
    val id = id ?: prefs?.getString(prefAttrName(PREF_ID), null)

    @JvmOverloads
    constructor(
        id: String?,
        baseAttrName: String = PREF_SESSION_BASE_NAME
    ) : this(prefs = null, id = id, baseAttrName = baseAttrName)

    @JvmOverloads
    constructor(
        prefs: SharedPreferences,
        baseAttrName: String = PREF_SESSION_BASE_NAME
    ) : this(prefs = prefs, id = null, baseAttrName = baseAttrName)

    protected fun prefAttrName(type: String) = "$baseAttrName.$type"

    open fun save(prefs: SharedPreferences.Editor) {
        prefs.putString(prefAttrName(PREF_ID), id)
    }

    open fun delete(prefs: SharedPreferences.Editor) {
        prefs.remove(prefAttrName(PREF_ID))
    }

    open val isValid get() = id != null

    override fun equals(other: Any?) = when {
        this === other -> true
        !(other is Session && javaClass == other.javaClass) -> false
        else -> id == other.id
    }

    override fun hashCode() = id?.hashCode() ?: 0
}
