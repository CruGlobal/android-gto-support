package org.ccci.gto.android.common.okta.authfoundation.credential

import androidx.annotation.VisibleForTesting
import com.okta.authfoundation.claims.DefaultClaimsProvider
import com.okta.authfoundation.client.OidcClientResult
import com.okta.authfoundation.client.dto.OidcUserInfo
import com.okta.authfoundation.credential.Credential
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.ccci.gto.android.common.base.TimeConstants
import org.ccci.gto.android.common.okta.authfoundation.claims.claims

fun Credential.isAuthenticatedFlow() = getTokenFlow().map { it != null }.distinctUntilChanged()
fun Credential.idTokenFlow() = getTokenFlow().map { idToken() }.distinctUntilChanged()
fun Credential.tagsFlow() = getTokenFlow().map { tags }.distinctUntilChanged()

// region userInfoFlow()
@VisibleForTesting
internal const val OIDC_USER_INFO = "org.ccci.gto.android.common.okta.authfoundation.credential.OidcUserInfo"
@VisibleForTesting
internal const val OIDC_USER_INFO_LOAD_TIME =
    "org.ccci.gto.android.common.okta.authfoundation.credential.OidcUserInfo+loadTime"

fun Credential.userInfoFlow() = flowOf(this).userInfoFlow()
@OptIn(ExperimentalCoroutinesApi::class)
fun Flow<Credential>.userInfoFlow() = transformLatest { cred ->
    coroutineScope {
        // coroutine that will periodically refresh the UserInfo from the API
        val loadAttempted = MutableStateFlow(false)
        launch {
            while (true) {
                if (
                    cred.tags.cachedUserInfo == null ||
                    cred.tags.nextRefreshTime < System.currentTimeMillis()
                ) {
                    cred.loadUserInfo()
                    loadAttempted.value = true
                }

                delay((cred.tags.nextRefreshTime - System.currentTimeMillis()).coerceAtLeast(TimeConstants.MIN_IN_MS))
            }
        }

        // emit UserInfo from the Credential tags
        val cachedInfoFlow = cred.tagsFlow()
            .map { it.cachedUserInfo }
            .distinctUntilChanged()
        emitAll(
            combine(cachedInfoFlow, loadAttempted) { data, loaded -> Pair(data, loaded) }
                .dropWhile { (data, loaded) -> data == null && !loaded }
                .map { (data) -> data }
                .onCompletion { if (it == null) emit(null) }
        )
    }
}.distinctUntilChanged()

private val Map<String, String>.cachedUserInfo get() = this[OIDC_USER_INFO]?.let {
    OidcUserInfo(
        DefaultClaimsProvider(
            Json.decodeFromString(JsonObject.serializer(), it),
            Json
        )
    )
}

private val Map<String, String>.nextRefreshTime
    get() = this[OIDC_USER_INFO_LOAD_TIME]?.toLongOrNull()?.let { it + TimeConstants.DAY_IN_MS } ?: 0

private suspend fun Credential.loadUserInfo() = when (val result = getUserInfo()) {
    is OidcClientResult.Success -> {
        storeToken(
            tags = tags + listOf(
                OIDC_USER_INFO to Json.encodeToString(JsonObject.serializer(), result.result.claims),
                OIDC_USER_INFO_LOAD_TIME to System.currentTimeMillis().toString()
            )
        )
        result.result
    }

    is OidcClientResult.Error -> {
        // TODO: should we do something with errors here?
        null
    }
}
// endregion userInfoFlow()
