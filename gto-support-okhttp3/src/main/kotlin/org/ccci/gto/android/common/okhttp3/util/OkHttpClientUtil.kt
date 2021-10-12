@file:JvmName("OkHttpClientUtil")

package org.ccci.gto.android.common.okhttp3.util

import okhttp3.Interceptor
import okhttp3.OkHttpClient

private val GLOBAL_NETWORK_INTERCEPTORS = mutableListOf<Interceptor>()
fun addGlobalNetworkInterceptor(interceptor: Interceptor) = GLOBAL_NETWORK_INTERCEPTORS.add(interceptor)
fun OkHttpClient.Builder.attachGlobalInterceptors() =
    apply { GLOBAL_NETWORK_INTERCEPTORS.forEach { addNetworkInterceptor(it) } }
