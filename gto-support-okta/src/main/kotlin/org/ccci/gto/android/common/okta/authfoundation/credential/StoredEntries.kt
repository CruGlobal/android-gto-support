package org.ccci.gto.android.common.okta.authfoundation.credential

import com.okta.authfoundation.credential.Token
import com.okta.authfoundation.credential.TokenStorage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class StoredEntries(@SerialName("entries") val entries: List<StoredEntry>) {
    @Serializable
    internal class StoredEntry(
        @SerialName("identifier") val identifier: String,
        @SerialName("token") val token: StoredToken?,
        @SerialName("tags") val tags: Map<String, String>,
    ) {
        constructor(entry: TokenStorage.Entry) : this(
            identifier = entry.identifier,
            token = entry.token?.let { StoredToken(it) },
            tags = entry.tags
        )

        fun asEntry() = TokenStorage.Entry(
            identifier = identifier,
            token = token?.asToken(),
            tags = tags
        )
    }

    @Serializable
    internal class StoredToken internal constructor(
        @SerialName("token_type") val tokenType: String,
        @SerialName("expires_in") val expiresIn: Int,
        @SerialName("access_token") val accessToken: String,
        @SerialName("scope") val scope: String? = null,
        @SerialName("refresh_token") val refreshToken: String? = null,
        @SerialName("id_token") val idToken: String? = null,
        @SerialName("device_secret") val deviceSecret: String? = null,
        @SerialName("issued_token_type") val issuedTokenType: String? = null,
    ) {
        constructor(token: Token) : this(
            tokenType = token.tokenType,
            expiresIn = token.expiresIn,
            accessToken = token.accessToken,
            scope = token.scope,
            refreshToken = token.refreshToken,
            idToken = token.idToken,
            deviceSecret = token.deviceSecret,
            issuedTokenType = token.issuedTokenType
        )

        fun asToken() = Token(
            tokenType = tokenType,
            expiresIn = expiresIn,
            accessToken = accessToken,
            scope = scope,
            refreshToken = refreshToken,
            idToken = idToken,
            deviceSecret = deviceSecret,
            issuedTokenType = issuedTokenType,
        )
    }

    internal companion object {
        fun from(entries: List<TokenStorage.Entry>) = StoredEntries(entries.map { StoredEntry(it) })
    }

    fun asTokenStorageEntries(): List<TokenStorage.Entry> = entries.map { it.asEntry() }
}
