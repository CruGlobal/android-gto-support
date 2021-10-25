@file:SuppressLint("RestrictedApi")

package com.okta.oidc.clients

import android.annotation.SuppressLint
import java.io.IOException

internal val AuthAPI.isCancelled
    get() = try {
        checkIfCanceled()
        false
    } catch (e: IOException) {
        true
    }
internal fun AuthAPI.resetCurrentStateInt() = resetCurrentState()
