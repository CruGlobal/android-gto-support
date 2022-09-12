package com.okta.authfoundation.claims;

import androidx.annotation.RestrictTo;

import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonObject;

@RestrictTo(RestrictTo.Scope.LIBRARY)
class DefaultClaimsProviderInternals {
    @SuppressWarnings("KotlinInternalInJava")
    static DefaultClaimsProvider create(JsonObject claims, Json json) {
        return new DefaultClaimsProvider(claims, json);
    }
}
