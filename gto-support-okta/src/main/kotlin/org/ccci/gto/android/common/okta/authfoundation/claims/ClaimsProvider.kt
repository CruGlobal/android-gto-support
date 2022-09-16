package org.ccci.gto.android.common.okta.authfoundation.claims

import com.okta.authfoundation.claims.ClaimsProvider
import kotlinx.serialization.json.JsonObject

internal val ClaimsProvider.claims get() = deserializeClaims(JsonObject.serializer())
