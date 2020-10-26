package org.ccci.gto.android.common.okta.oidc.net.response

import android.annotation.SuppressLint
import com.okta.oidc.net.response.UserInfo
import com.okta.oidc.storage.Persistable
import org.ccci.gto.android.common.base.TimeConstants
import org.ccci.gto.android.common.okta.oidc.RETRIEVED_AT
import org.json.JSONObject

@SuppressLint("RestrictedApi")
internal class PersistableUserInfo(val oktaId: String, val userInfo: UserInfo, val raw: String) : Persistable {
    constructor(oktaId: String, userInfo: UserInfo) : this(oktaId, userInfo, userInfo.raw?.toString().orEmpty())
    constructor(oktaId: String, raw: String) : this(oktaId, UserInfo(JSONObject(raw)), raw)

    private val retrievedAt = (userInfo.get(RETRIEVED_AT) as? Number)?.toLong()

    val nextRefreshTime =
        retrievedAt?.let { it + TimeConstants.DAY_IN_MS } ?: System.currentTimeMillis() + TimeConstants.HOUR_IN_MS

    val isStale get() = System.currentTimeMillis() > nextRefreshTime

    override fun getKey() = buildKey(oktaId)
    override fun persist() = raw

    data class Restore(private val oktaUserId: String) : Persistable.Restore<PersistableUserInfo> {
        override fun getKey() = buildKey(oktaUserId)
        override fun restore(data: String?) = data?.let { PersistableUserInfo(oktaUserId, it) }
    }
}

private fun buildKey(oktaUserId: String) = "PersistableUserInfo:$oktaUserId"
