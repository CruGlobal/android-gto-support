package com.okta.authfoundation.claims

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Suppress("ktlint:standard:function-naming")
internal fun DefaultClaimsProvider(claims: JsonObject, json: Json) = DefaultClaimsProviderInternals.create(claims, json)
