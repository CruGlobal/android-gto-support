package com.okta.authfoundation.claims

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal fun DefaultClaimsProvider(claims: JsonObject, json: Json) = DefaultClaimsProviderInternals.create(claims, json)
