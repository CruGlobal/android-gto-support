@file:SuppressLint("RestrictedApi")

package com.okta.oidc.clients

import android.annotation.SuppressLint
import java.io.IOException

internal val AuthAPI.isCancelled get() = mCancel.get()
internal fun AuthAPI.resetCurrentStateInt() = resetCurrentState()
