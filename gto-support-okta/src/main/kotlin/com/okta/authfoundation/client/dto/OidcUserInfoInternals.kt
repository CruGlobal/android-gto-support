package com.okta.authfoundation.client.dto

import com.okta.authfoundation.claims.ClaimsProvider

internal fun OidcUserInfo(claimsProvider: ClaimsProvider) = OidcUserInfoInternals.create(claimsProvider)
