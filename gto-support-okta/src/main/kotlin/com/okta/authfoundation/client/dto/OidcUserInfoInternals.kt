package com.okta.authfoundation.client.dto

import com.okta.authfoundation.claims.ClaimsProvider

@Suppress("ktlint:standard:function-naming")
internal fun OidcUserInfo(claimsProvider: ClaimsProvider) = OidcUserInfoInternals.create(claimsProvider)
