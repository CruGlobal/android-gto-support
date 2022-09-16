package com.okta.authfoundation.client.dto;

import com.okta.authfoundation.claims.ClaimsProvider;

class OidcUserInfoInternals {
    static OidcUserInfo create(ClaimsProvider claimsProvider) {
        return new OidcUserInfo(claimsProvider);
    }
}
