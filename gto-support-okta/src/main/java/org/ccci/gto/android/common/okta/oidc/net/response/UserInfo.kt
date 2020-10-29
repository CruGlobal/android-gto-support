package org.ccci.gto.android.common.okta.oidc.net.response

import androidx.annotation.VisibleForTesting
import com.okta.oidc.net.response.UserInfo

@VisibleForTesting
internal const val CLAIM_OKTA_USER_ID = "sub"
private const val CLAIM_SSO_GUID = "ssoGuid"
private const val CLAIM_EMAIL = "email"
private const val CLAIM_GIVEN_NAME = "given_name"
private const val CLAIM_FAMILY_NAME = "family_name"
private const val CLAIM_GR_MASTER_PERSON_ID = "grMasterPersonId"

val UserInfo.oktaUserId get() = get(CLAIM_OKTA_USER_ID)?.toString()
val UserInfo.ssoGuid get() = get(CLAIM_SSO_GUID)?.toString()
val UserInfo.email get() = get(CLAIM_EMAIL)?.toString()
val UserInfo.givenName get() = get(CLAIM_GIVEN_NAME)?.toString()
val UserInfo.familyName get() = get(CLAIM_FAMILY_NAME)?.toString()
val UserInfo.grMasterPersonId get() = get(CLAIM_GR_MASTER_PERSON_ID)?.toString()
