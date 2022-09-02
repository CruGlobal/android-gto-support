package org.ccci.gto.android.common.okta.oidc.net.response

import android.annotation.SuppressLint
import com.okta.oidc.net.response.UserInfo
import com.okta.oidc.storage.Persistable
import org.ccci.gto.android.common.base.TimeConstants.DAY_IN_MS
import org.json.JSONObject

private const val CLAIM_RETRIEVED_AT = "retrieved_at"

@SuppressLint("RestrictedApi")
internal class PersistableUserInfo(
    private val oktaUserId: String,
    val userInfo: UserInfo,
    private val retrievedAt: Long = System.currentTimeMillis()
) : Persistable {
    private constructor(oktaUserId: String, raw: JSONObject) :
        this(oktaUserId, UserInfo(raw), raw.optLong(CLAIM_RETRIEVED_AT, System.currentTimeMillis()))

    private val nextRefreshTime = retrievedAt + DAY_IN_MS

    val isStale get() = System.currentTimeMillis() > nextRefreshTime
    val nextRefreshDelay get() = nextRefreshTime - System.currentTimeMillis()

    override fun getKey() = buildKey(oktaUserId)
    override fun persist() = (userInfo.raw ?: JSONObject()).put(CLAIM_RETRIEVED_AT, retrievedAt).toString()

    data class Restore(private val oktaUserId: String) : Persistable.Restore<PersistableUserInfo> {
        override fun getKey() = buildKey(oktaUserId)
        override fun restore(data: String?) = data?.let { PersistableUserInfo(oktaUserId, JSONObject(it)) }
    }
}

private fun buildKey(oktaUserId: String) = "PersistableUserInfo:$oktaUserId"
