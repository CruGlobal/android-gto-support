@file:SuppressLint("RestrictedApi")
package org.ccci.gto.android.common.okta.oidc.net.response

import android.annotation.SuppressLint
import com.okta.oidc.net.response.TokenResponse
import com.okta.oidc.net.response.expires_in
import java.lang.NumberFormatException

internal fun TokenResponse.repair() = apply {
    try {
        Integer.parseInt(expiresIn)
    } catch (e: NumberFormatException) {
        expires_in = "0"
    }
}
