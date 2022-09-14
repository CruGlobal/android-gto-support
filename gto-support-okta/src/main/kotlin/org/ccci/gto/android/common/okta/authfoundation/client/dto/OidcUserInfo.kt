package org.ccci.gto.android.common.okta.authfoundation.client.dto

import com.okta.authfoundation.claims.subject
import com.okta.authfoundation.client.dto.OidcUserInfo
import kotlinx.serialization.builtins.serializer

private const val CLAIM_SSO_GUID = "ssoGuid"
private const val CLAIM_GR_MASTER_PERSON_ID = "grMasterPersonId"

inline val OidcUserInfo.oktaUserId get() = subject
val OidcUserInfo.ssoGuid get() = deserializeClaim(CLAIM_SSO_GUID, String.serializer())
val OidcUserInfo.grMasterPersonId get() = deserializeClaim(CLAIM_GR_MASTER_PERSON_ID, String.serializer())
