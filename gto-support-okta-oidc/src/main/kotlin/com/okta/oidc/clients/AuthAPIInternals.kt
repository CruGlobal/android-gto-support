@file:SuppressLint("RestrictedApi")

package com.okta.oidc.clients

import android.annotation.SuppressLint

internal val AuthAPI.isCancelled get() = mCancel.get()
internal fun AuthAPI.resetCurrentStateInt() = resetCurrentState()
