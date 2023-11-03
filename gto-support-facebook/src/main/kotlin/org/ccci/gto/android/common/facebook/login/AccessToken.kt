package org.ccci.gto.android.common.facebook.login

import com.facebook.AccessToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

fun AccessToken.isExpiredFlow() = flow {
    while (!isExpired) {
        emit(true)
        delay((expires.time - System.currentTimeMillis()).coerceAtLeast(1))
    }
    emit(false)
}.distinctUntilChanged()
